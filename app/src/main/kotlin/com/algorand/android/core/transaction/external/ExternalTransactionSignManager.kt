/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.core.transaction.external

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.algorand.android.ledger.CustomScanCallback
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.ExternalTransaction
import com.algorand.android.ledger.operations.ExternalTransactionOperation
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.LedgerBleResult
import com.algorand.android.models.LedgerBleResult.AppErrorResult
import com.algorand.android.models.LedgerBleResult.LedgerErrorResult
import com.algorand.android.models.LedgerBleResult.OperationCancelledResult
import com.algorand.android.models.LedgerBleResult.SignedTransactionResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionQueuingHelper
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.NotInitialized
import com.algorand.android.utils.Event
import com.algorand.android.utils.LifecycleScopedCoroutineOwner
import com.algorand.android.utils.ListQueuingHelper
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.signTx
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import javax.inject.Inject
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ExternalTransactionSignManager<TRANSACTION : ExternalTransaction> @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val getLocalAccount: GetLocalAccount,
    private val signHdKeyTransaction: SignHdKeyTransaction
) : LifecycleScopedCoroutineOwner() {

    private val _signResultFlow = MutableStateFlow<ExternalTransactionSignResult>(NotInitialized)
    protected val signResultFlow: StateFlow<ExternalTransactionSignResult>
        get() = _signResultFlow

    protected var transaction: List<TRANSACTION>? = null

    private val signHelperListener = object : ListQueuingHelper.Listener<ExternalTransaction, ByteArray> {
        override fun onAllItemsDequeued(signedTransactions: List<ByteArray?>) {
            transaction?.run {
                _signResultFlow.value = ExternalTransactionSignResult.Success(this, signedTransactions)
            }
        }

        override fun onNextItemToBeDequeued(
            transaction: ExternalTransaction,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            transaction.signTransaction(
                currentTransactionIndex = currentItemIndex,
                totalTransactionCount = totalItemCount
            )
        }
    }

    private val scanCallback = object : CustomScanCallback() {

        override fun onLedgerScanned(
            device: BluetoothDevice,
            currentTransactionIndex: Int?,
            totalTransactionCount: Int?
        ) {
            ledgerBleSearchManager.stop()
            currentScope.launch {
                externalTransactionQueuingHelper.currentItem?.run {
                    val swapTransactionOperation = ExternalTransactionOperation(device, this)
                    ledgerBleOperationManager.startLedgerOperation(
                        swapTransactionOperation,
                        currentTransactionIndex,
                        totalTransactionCount
                    )
                }
            }
        }

        override fun onScanError(errorMessageResId: Int, titleResId: Int) {
            postResult(ExternalTransactionSignResult.LedgerScanFailed)
        }
    }

    private val operationManagerCollectorAction: (suspend (Event<LedgerBleResult>?) -> Unit) = { ledgerBleResultEvent ->
        ledgerBleResultEvent?.consume()?.let { ledgerBleResult ->
            if (transaction == null) return@let
            when (ledgerBleResult) {
                is LedgerBleResult.LedgerWaitingForApproval -> {
                    ExternalTransactionSignResult.LedgerWaitingForApproval(
                        ledgerName = ledgerBleResult.bluetoothName,
                        currentTransactionIndex = ledgerBleResult.currentTransactionIndex,
                        totalTransactionCount = ledgerBleResult.totalTransactionCount,
                        isTransactionIndicatorVisible = ledgerBleResult.totalTransactionCount != null &&
                            ledgerBleResult.currentTransactionIndex != null &&
                            ledgerBleResult.totalTransactionCount > 1
                    ).apply(::postResult)
                }
                is SignedTransactionResult -> {
                    externalTransactionQueuingHelper.currentItem?.run {
                        onTransactionSigned(this, ledgerBleResult.transactionByteArray)
                    }
                }
                is LedgerErrorResult -> {
                    postResult(ExternalTransactionSignResult.Error.Api(ledgerBleResult.errorMessage))
                }
                is AppErrorResult -> postResult(
                    ExternalTransactionSignResult.Error.Defined(
                        AnnotatedString(ledgerBleResult.errorMessageId),
                        ledgerBleResult.titleResId
                    )
                )
                is OperationCancelledResult -> postResult(ExternalTransactionSignResult.TransactionCancelled())
                else -> {
                    sendErrorLog("Unhandled else case in WalletConnectSignManager.operationManagerCollectorAction")
                }
            }
        }
    }

    fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        setupLedgerOperationManager(lifecycle)
        externalTransactionQueuingHelper.initListener(signHelperListener)
    }

    open fun signTransaction(transaction: List<TRANSACTION>) {
        postResult(ExternalTransactionSignResult.Loading)
        this.transaction = transaction
        externalTransactionQueuingHelper.initItemsToBeEnqueued(transaction)
    }

    private fun ExternalTransaction.signTransaction(
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?
    ) {
        currentScope.launch {
            when (val transactionSigner = getTransactionSigner(accountAddress)) {
                is TransactionSigner.SignerNotFound -> {
                    externalTransactionQueuingHelper.cacheDequeuedItem(null)
                }
                is TransactionSigner.Algo25 -> {
                    signTransactionWithSecretKey(this@signTransaction, getAlgo25SecretKey(transactionSigner.address)!!)
                }
                is TransactionSigner.HdKey -> {
                    signHdTransaction(this@signTransaction, transactionSigner.address)
                }
                is TransactionSigner.LedgerBle -> {
                    sendTransactionWithLedger(transactionSigner, currentTransactionIndex, totalTransactionCount)
                }
            }
        }
    }

    private fun sendTransactionWithLedger(
        ledgerDetail: TransactionSigner.LedgerBle,
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?
    ) {
        val bluetoothAddress = ledgerDetail.bluetoothAddress
        val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
        if (currentConnectedDevice != null && currentConnectedDevice.address == bluetoothAddress) {
            sendCurrentTransaction(
                bluetoothDevice = currentConnectedDevice,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )
        } else {
            searchForDevice(
                ledgerAddress = bluetoothAddress,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )
        }
    }

    private fun sendCurrentTransaction(
        bluetoothDevice: BluetoothDevice,
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?
    ) {
        externalTransactionQueuingHelper.currentItem?.run {
            val externalTransactionOperation = ExternalTransactionOperation(bluetoothDevice, this)
            ledgerBleOperationManager.startLedgerOperation(
                newOperation = externalTransactionOperation,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )
        }
    }

    private fun searchForDevice(
        ledgerAddress: String,
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?
    ) {
        ledgerBleSearchManager.scan(
            newScanCallback = scanCallback,
            currentTransactionIndex = currentTransactionIndex,
            totalTransactionCount = totalTransactionCount,
            filteredAddress = ledgerAddress,
            coroutineScope = currentScope
        )
    }

    private fun signTransactionWithSecretKey(transaction: ExternalTransaction, secretKey: ByteArray) {
        val signedTransaction = transaction.transactionByteArray?.signTx(secretKey)
        onTransactionSigned(transaction, signedTransaction)
    }

    private suspend fun signHdTransaction(transaction: ExternalTransaction, accountAddress: String) {
        val transactionBytes = transaction.transactionByteArray ?: return handleSignError(transaction)
        val hdKey = getLocalAccount(accountAddress) as? LocalAccount.HdKey ?: return handleSignError(transaction)
        val seed = getHdSeed(seedId = hdKey.seedId) ?: return handleSignError(transaction)

        val transactionSignedByteArray = signHdKeyTransaction.signTransaction(
            transactionBytes, seed.copyOf(), hdKey.account, hdKey.change, hdKey.keyIndex
        ) ?: return handleSignError(transaction)

        seed.clearFromMemory()
        onTransactionSigned(transaction, transactionSignedByteArray)
    }

    private fun handleSignError(transaction: ExternalTransaction) {
        onTransactionSigned(transaction, null)
    }

    protected open fun onTransactionSigned(transaction: ExternalTransaction, signedTransaction: ByteArray?) {
        externalTransactionQueuingHelper.cacheDequeuedItem(signedTransaction)
    }

    private fun postResult(transactionSignResult: ExternalTransactionSignResult) {
        _signResultFlow.value = transactionSignResult
    }

    private fun setupLedgerOperationManager(lifecycle: Lifecycle) {
        ledgerBleOperationManager.setup(lifecycle)
        lifecycle.coroutineScope.launch {
            ledgerBleOperationManager.ledgerBleResultFlow.collect { operationManagerCollectorAction.invoke(it) }
        }
    }

    override fun stopAllResources() {
        ledgerBleSearchManager.stop()
        externalTransactionQueuingHelper.clearCachedData()
        transaction = null
    }

    fun manualStopAllResources() {
        this.stopAllResources()
        currentScope.coroutineContext.cancelChildren()
        ledgerBleOperationManager.manualStopAllProcess()
    }
}
