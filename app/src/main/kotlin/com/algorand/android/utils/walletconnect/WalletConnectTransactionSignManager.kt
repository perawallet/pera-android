/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.utils.walletconnect

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.coroutineScope
import android.content.Intent
import com.algorand.android.core.transaction.JointAccountTransactionSignHelper
import com.algorand.android.core.transaction.sync.JointAccountSyncSignDependencies
import com.algorand.android.core.transaction.sync.JointAccountSyncSignForegroundService
import com.algorand.android.core.transaction.sync.SyncSignResultHolder
import com.algorand.android.ledger.CustomScanCallback
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.WalletConnectTransactionOperation
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.BaseWalletConnectTransaction
import com.algorand.android.models.LedgerBleResult
import com.algorand.android.models.LedgerBleResult.AppErrorResult
import com.algorand.android.models.LedgerBleResult.LedgerErrorResult
import com.algorand.android.models.LedgerBleResult.OperationCancelledResult
import com.algorand.android.models.LedgerBleResult.SignedTransactionResult
import com.algorand.android.models.WalletConnectRequest.WalletConnectTransaction
import com.algorand.android.models.WalletConnectSignResult
import com.algorand.android.models.WalletConnectSignResult.Error.Api
import com.algorand.android.models.WalletConnectSignResult.Error.Defined
import com.algorand.android.models.WalletConnectSignResult.LedgerWaitingForApproval
import com.algorand.android.models.WalletConnectSignResult.Success
import com.algorand.android.models.WalletConnectSignResult.TransactionCancelledByLedger
import com.algorand.android.utils.Event
import com.algorand.android.utils.LifecycleScopedCoroutineOwner
import com.algorand.android.utils.ListQueuingHelper
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.signTx
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

class WalletConnectTransactionSignManager @Inject constructor(
    private val walletConnectSignValidator: WalletConnectSignValidator,
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val signHelper: WalletConnectTransactionSignHelper,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val getLocalAccount: GetLocalAccount,
    private val signHdKeyTransaction: SignHdKeyTransaction,
    private val syncSignDependencies: JointAccountSyncSignDependencies
) : LifecycleScopedCoroutineOwner() {

    private val jointAccountTransactionSignHelper get() = syncSignDependencies.jointAccountTransactionSignHelper
    private val signArbitraryDataForSyncRequest get() = syncSignDependencies.signArbitraryDataForSyncRequest
    private val syncSignRequestPollingManager get() = syncSignDependencies.syncSignRequestPollingManager
    private val multisigTransactionAssembler get() = syncSignDependencies.multisigTransactionAssembler
    private val applicationContext get() = syncSignDependencies.applicationContext
    private val syncSignResultHolder get() = syncSignDependencies.syncSignResultHolder

    val signResultLiveData: LiveData<WalletConnectSignResult>
        get() = _signResultLiveData
    private val _signResultLiveData = MutableLiveData<WalletConnectSignResult>()

    private var transaction: WalletConnectTransaction? = null

    private val signHelperListener = object : ListQueuingHelper.Listener<BaseWalletConnectTransaction, ByteArray> {
        override fun onAllItemsDequeued(dequeuedItemList: List<ByteArray?>) {
            transaction?.run {
                _signResultLiveData.postValue(Success(session.sessionIdentifier, requestId, dequeuedItemList))
            }
        }

        override fun onNextItemToBeDequeued(
            item: BaseWalletConnectTransaction,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            currentScope.launch {
                item.signTransaction(
                    currentTransactionIndex = currentItemIndex,
                    totalTransactionCount = totalItemCount
                )
            }
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
                signHelper.currentItem?.run {
                    val walletConnectTransactionOperation = WalletConnectTransactionOperation(device, this)
                    ledgerBleOperationManager.startLedgerOperation(
                        walletConnectTransactionOperation,
                        currentTransactionIndex,
                        totalTransactionCount
                    )
                }
            }
        }

        override fun onScanError(errorMessageResId: Int, titleResId: Int) {
            postResult(WalletConnectSignResult.LedgerScanFailed)
        }
    }

    private val operationManagerCollectorAction: (suspend (Event<LedgerBleResult>?) -> Unit) = { ledgerBleResultEvent ->
        ledgerBleResultEvent?.consume()?.run {
            if (transaction == null) return@run
            when (this) {
                is LedgerBleResult.LedgerWaitingForApproval -> {
                    LedgerWaitingForApproval(
                        ledgerName = bluetoothName,
                        currentTransactionIndex = currentTransactionIndex,
                        totalTransactionCount = totalTransactionCount,
                        isTransactionIndicatorVisible = totalTransactionCount != null &&
                                currentTransactionIndex != null &&
                                totalTransactionCount > 1
                    ).apply(::postResult)
                }

                is SignedTransactionResult -> signHelper.cacheDequeuedItem(transactionByteArray)
                is LedgerErrorResult -> postResult(Api(errorMessage))
                is AppErrorResult -> postResult(Defined(AnnotatedString(errorMessageId), titleResId))
                is OperationCancelledResult -> postResult(TransactionCancelledByLedger())
                else -> {
                    sendErrorLog("Unhandled else case in WalletConnectSignManager.operationManagerCollectorAction")
                }
            }
        }
    }

    fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        setupLedgerOperationManager(lifecycle)
        signHelper.initListener(signHelperListener)
    }

    fun signTransaction(transaction: WalletConnectTransaction) {
        postResult(WalletConnectSignResult.Loading)
        this.transaction = transaction
        with(transaction) {
            when (val result = walletConnectSignValidator.canTransactionBeSigned(this)) {
                is WalletConnectSignResult.CanBeSigned -> {
                    signHelper.initItemsToBeEnqueued(transactionList.flatten())
                }

                is WalletConnectSignResult.Error -> postResult(result)
                else -> {
                    sendErrorLog("Unhandled else case in WalletConnectSignManager.signTransaction")
                }
            }
        }
    }

    private suspend fun BaseWalletConnectTransaction.signTransaction(
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?
    ) {
        when (transactionSigner) {
            is TransactionSigner.Algo25 -> handleAlgo25TransactionSigning()
            is TransactionSigner.HdKey -> handleHdKeyTransactionSigning()
            is TransactionSigner.LedgerBle -> sendTransactionWithLedger(
                ledgerDetail = transactionSigner as TransactionSigner.LedgerBle,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )

            is TransactionSigner.Joint -> handleJointTransactionSigning()
            else -> cacheNullDequeuedItem()
        }
    }

    private suspend fun BaseWalletConnectTransaction.handleJointTransactionSigning() {
        val wcTx = transaction ?: return cacheNullDequeuedItem()
        val jointAddress = (transactionSigner as? TransactionSigner.Joint)?.address ?: return cacheNullDequeuedItem()
        val allTxs = wcTx.transactionList.flatten()
        val rawBytesList = allTxs.mapNotNull { it.decodedTransaction }
        if (rawBytesList.size != allTxs.size) return cacheNullDequeuedItem()

        val result = jointAccountTransactionSignHelper.handleSyncJointAccountTransactionWithRawBytes(
            jointAccountAddress = jointAddress,
            rawTransactionBytesList = rawBytesList
        )
        when (result) {
            is JointAccountTransactionSignHelper.JointSignResult.SyncPending -> {
                val arbitrarySign = signArbitraryDataForSyncRequest(
                    result.signRequestId,
                    result.proposerAddress
                ) ?: return cacheNullDequeuedItem()
                val signRequestId = result.signRequestId
                startSyncSignForegroundService(
                    signRequestId = signRequestId,
                    proposerAddress = result.proposerAddress,
                    arbitraryDataSignOfId = arbitrarySign
                )
                val threshold = (getLocalAccount(jointAddress) as? LocalAccount.Joint)?.threshold ?: 2
                postResult(
                    WalletConnectSignResult.WaitingForJointSignatures(
                        signRequestId = signRequestId,
                        signedCount = 1,
                        threshold = threshold
                    )
                )
                collectSyncSignResultAndComplete(signRequestId)
            }

            else -> cacheNullDequeuedItem()
        }
    }

    private fun startSyncSignForegroundService(
        signRequestId: String,
        proposerAddress: String,
        arbitraryDataSignOfId: String
    ) {
        val intent = Intent(applicationContext, JointAccountSyncSignForegroundService::class.java).apply {
            putExtra(JointAccountSyncSignForegroundService.EXTRA_SIGN_REQUEST_ID, signRequestId)
            putExtra(JointAccountSyncSignForegroundService.EXTRA_PROPOSER_ADDRESS, proposerAddress)
            putExtra(
                JointAccountSyncSignForegroundService.EXTRA_ARBITRARY_DATA_SIGN_OF_ID,
                arbitraryDataSignOfId
            )
        }
        applicationContext.startForegroundService(intent)
    }

    private suspend fun collectSyncSignResultAndComplete(signRequestId: String) {
        syncSignResultHolder.events
            .filter { it.signRequestId == signRequestId }
            .take(1)
            .collect { event ->
                syncSignResultHolder.consumeResult(signRequestId)
                handleSyncSignResult(event.result)
            }
    }

    private fun handleSyncSignResult(result: SyncSignResultHolder.SyncSignResult) {
        when (result) {
            is SyncSignResultHolder.SyncSignResult.SignaturesReady ->
                processSignaturesReadyAndCache(result.signRequest)
            is SyncSignResultHolder.SyncSignResult.Failed,
            is SyncSignResultHolder.SyncSignResult.Expired,
            is SyncSignResultHolder.SyncSignResult.Declined -> postDeclinedOrFailedResult()
        }
    }

    private fun postDeclinedOrFailedResult() {
        signHelper.clearCachedData()
        _signResultLiveData.postValue(WalletConnectSignResult.JointSignRequestRejected())
    }

    private fun processSignaturesReadyAndCache(signRequest: SignRequestWithFullSignature) {
        val firstList = signRequest.transactionLists?.firstOrNull()
        val rawTransactions = firstList?.rawTransactions
        val responses = firstList?.responses
        val jointAccount = signRequest.jointAccount
        if (!hasRequiredDataForAssemble(rawTransactions, responses, jointAccount)) {
            cacheNullDequeuedItem()
            return
        }
        val account = jointAccount!!
        val participantAddresses = account.participantAddresses!!
        val version = account.version!!
        val threshold = account.threshold!!
        when (val assembleResult = multisigTransactionAssembler.assemble(
            rawTransactionsBase64 = rawTransactions!!,
            participantAddresses = participantAddresses,
            version = version,
            threshold = threshold,
            responses = responses!!
        )) {
            is PeraResult.Success -> assembleResult.data.forEach { signHelper.cacheDequeuedItem(it) }
            is PeraResult.Error -> cacheNullDequeuedItem()
        }
    }

    private fun hasRequiredDataForAssemble(
        rawTransactions: List<String>?,
        responses: List<ParticipantSignature>?,
        jointAccount: JointAccount?
    ): Boolean {
        if (rawTransactions == null || responses == null || jointAccount == null) return false
        return jointAccount.participantAddresses != null &&
            jointAccount.version != null &&
            jointAccount.threshold != null
    }

    private suspend fun BaseWalletConnectTransaction.handleAlgo25TransactionSigning() {
        val signerAddress = transactionSigner?.address ?: return cacheNullDequeuedItem()
        val secretKey = getAlgo25SecretKey(signerAddress) ?: return cacheNullDequeuedItem()
        val signedTransaction = decodedTransaction?.signTx(secretKey) ?: return cacheNullDequeuedItem()
        signHelper.cacheDequeuedItem(signedTransaction)
    }

    private suspend fun BaseWalletConnectTransaction.handleHdKeyTransactionSigning() {
        val signerAddress = transactionSigner?.address ?: return cacheNullDequeuedItem()
        val transactionBytes = decodedTransaction ?: return cacheNullDequeuedItem()

        getLocalAccount(signerAddress).let { localAccount ->
            when (localAccount) {
                is LocalAccount.HdKey -> signHdKeyTransaction(transactionBytes, localAccount)
                else -> return cacheNullDequeuedItem()
            }
        }
    }

    private suspend fun signHdKeyTransaction(
        transactionBytes: ByteArray,
        localAccount: LocalAccount.HdKey
    ) {
        val seed = this.getHdSeed(seedId = localAccount.seedId) ?: return cacheNullDequeuedItem()

        val transactionSignedByteArray = signHdKeyTransaction.signTransaction(
            transactionBytes,
            seed.copyOf(),
            localAccount.account,
            localAccount.change,
            localAccount.keyIndex
        ) ?: return cacheNullDequeuedItem()
        seed.clearFromMemory()
        signHelper.cacheDequeuedItem(transactionSignedByteArray)
    }

    private fun cacheNullDequeuedItem() {
        signHelper.cacheDequeuedItem(null)
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
        signHelper.currentItem?.run {
            val walletConnectTransactionOperation = WalletConnectTransactionOperation(bluetoothDevice, this)
            ledgerBleOperationManager.startLedgerOperation(
                newOperation = walletConnectTransactionOperation,
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

    private fun postResult(walletConnectSignResult: WalletConnectSignResult) {
        _signResultLiveData.postValue(walletConnectSignResult)
    }

    private fun setupLedgerOperationManager(lifecycle: Lifecycle) {
        ledgerBleOperationManager.setup(lifecycle)
        lifecycle.coroutineScope.launch {
            ledgerBleOperationManager.ledgerBleResultFlow.collect { operationManagerCollectorAction.invoke(it) }
        }
    }

    override fun stopAllResources() {
        syncSignRequestPollingManager.stopPolling()
        ledgerBleSearchManager.stop()
        signHelper.clearCachedData()
        transaction = null
    }

    fun manualStopAllResources() {
        this.stopAllResources()
        currentScope.coroutineContext.cancelChildren()
        ledgerBleOperationManager.manualStopAllProcess()
    }
}
