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

package com.algorand.android.core.transaction.external

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.algorand.android.R
import com.algorand.android.core.transaction.JointAccountTransactionSignHelper
import com.algorand.android.core.transaction.sync.JointAccountSyncSignForegroundService
import com.algorand.android.core.transaction.sync.JointAccountSyncSignDependencies
import com.algorand.android.core.transaction.sync.SyncSignResultHolder
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
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignature
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

open class ExternalTransactionSignManager<TRANSACTION : ExternalTransaction> @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    private val getTransactionSigner: GetTransactionSigner,
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

    private val _signResultFlow = MutableStateFlow<ExternalTransactionSignResult>(NotInitialized)
    protected val signResultFlow: StateFlow<ExternalTransactionSignResult>
        get() = _signResultFlow

    protected var transaction: List<TRANSACTION>? = null

    private val signHelperListener = object : ListQueuingHelper.Listener<ExternalTransaction, ByteArray> {
        override fun onAllItemsDequeued(dequeuedItemList: List<ByteArray?>) {
            transaction?.run {
                _signResultFlow.value = ExternalTransactionSignResult.Success(this, dequeuedItemList)
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
                    val errorMessage =
                        "Unhandled else case in ExternalTransactionSignManager.operationManagerCollectorAction"
                    sendErrorLog(errorMessage)
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
                    signTransactionWithSecretKey(
                        this@signTransaction,
                        getAlgo25SecretKey(transactionSigner.address) ?: return@launch
                    )
                }

                is TransactionSigner.HdKey -> {
                    signHdTransaction(this@signTransaction, transactionSigner.address)
                }

                is TransactionSigner.LedgerBle -> {
                    sendTransactionWithLedger(transactionSigner, currentTransactionIndex, totalTransactionCount)
                }

                is TransactionSigner.Joint -> {
                    signJointAccountTransaction()
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
        try {
            val signedTransaction = transaction.transactionByteArray?.signTx(secretKey)
            onTransactionSigned(transaction, signedTransaction)
        } finally {
            secretKey.clearFromMemory()
        }
    }

    private suspend fun signHdTransaction(transaction: ExternalTransaction, accountAddress: String) {
        val transactionBytes = transaction.transactionByteArray ?: return handleSignError(transaction)
        val hdKey = getLocalAccount(accountAddress) as? LocalAccount.HdKey ?: return handleSignError(transaction)
        val seed = getHdSeed(seedId = hdKey.seedId) ?: return handleSignError(transaction)

        try {
            val transactionSignedByteArray = signHdKeyTransaction.signTransaction(
                transactionBytes, seed, hdKey.account, hdKey.change, hdKey.keyIndex
            ) ?: return handleSignError(transaction)
            onTransactionSigned(transaction, transactionSignedByteArray)
        } finally {
            seed.clearFromMemory()
        }
    }

    private fun signJointAccountTransaction() {
        val currentItem = externalTransactionQueuingHelper.currentItem
        if (currentItem == null) {
            Log.e(TAG, "signJoint: currentItem is null")
            return
        }
        val jointAccountAddress = currentItem.accountAddress
        val txList = transaction
        if (txList == null) {
            Log.e(TAG, "signJoint: transaction list is null")
            return
        }
        Log.d(TAG, "signJoint: address=$jointAccountAddress, txCount=${txList.size}")
        val rawBytesGroups = buildRawBytesGroups(txList)
        if (rawBytesGroups == null) {
            Log.e(TAG, "signJoint: buildRawBytesGroups returned null")
            return postJointSyncError()
        }
        Log.d(TAG, "signJoint: groups=${rawBytesGroups.size}, sizes=${rawBytesGroups.map { it.size }}")
        currentScope.launch {
            val result = jointAccountTransactionSignHelper
                .handleSyncJointAccountTransactionWithRawByteGroups(
                    jointAccountAddress = jointAccountAddress,
                    rawTransactionBytesGroups = rawBytesGroups
                )
            Log.d(TAG, "signJoint: helperResult=$result")
            when (result) {
                is JointAccountTransactionSignHelper.JointSignResult.SyncPending -> {
                    handleSyncPendingResult(result, jointAccountAddress, txList)
                }

                else -> {
                    Log.e(TAG, "signJoint: unexpected result=$result")
                    postJointSyncError()
                }
            }
        }
    }

    private suspend fun handleSyncPendingResult(
        result: JointAccountTransactionSignHelper.JointSignResult.SyncPending,
        jointAccountAddress: String,
        txList: List<TRANSACTION>
    ) {
        Log.d(TAG, "syncPending: id=${result.signRequestId}")
        val arbitrarySign = signArbitraryDataForSyncRequest(
            result.signRequestId,
            result.proposerAddress
        )
        if (arbitrarySign == null) {
            Log.e(TAG, "syncPending: arbitrarySign is null")
            return postJointSyncError()
        }
        val signRequestId = result.signRequestId
        startSyncSignForegroundService(
            signRequestId = signRequestId,
            proposerAddress = result.proposerAddress,
            arbitraryDataSignOfId = arbitrarySign
        )
        val jointAccount = getLocalAccount(jointAccountAddress)
        val threshold = (jointAccount as? LocalAccount.Joint)?.threshold ?: 0
        postResult(
            ExternalTransactionSignResult.WaitingForJointSignatures(
                signRequestId = signRequestId,
                signedCount = 1,
                threshold = threshold
            )
        )
        collectSyncSignResultAndComplete(signRequestId, txList)
    }

    protected open fun buildRawBytesGroups(txList: List<TRANSACTION>): List<List<ByteArray>>? {
        val grouped = txList.groupBy { it.groupIndex }
        val sortedKeys = grouped.keys.sorted()
        return sortedKeys.map { key ->
            val groupTxns = grouped[key] ?: return null
            val rawBytes = groupTxns.mapNotNull { it.transactionByteArray }
            if (rawBytes.size != groupTxns.size) return null
            rawBytes
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

    private fun collectSyncSignResultAndComplete(signRequestId: String, txList: List<TRANSACTION>) {
        currentScope.launch {
            syncSignResultHolder.events
                .filter { it.signRequestId == signRequestId }
                .take(1)
                .collect { event ->
                    syncSignResultHolder.consumeResult(signRequestId)
                    handleSyncSignResult(event.result, txList)
                }
        }
    }

    private fun handleSyncSignResult(
        result: SyncSignResultHolder.SyncSignResult,
        txList: List<TRANSACTION>
    ) {
        Log.d(TAG, "handleSyncSignResult: result=$result")
        when (result) {
            is SyncSignResultHolder.SyncSignResult.SignaturesReady ->
                assembleAndPostSuccess(result.signRequest, txList)
            is SyncSignResultHolder.SyncSignResult.Failed,
            is SyncSignResultHolder.SyncSignResult.Expired,
            is SyncSignResultHolder.SyncSignResult.Declined -> {
                Log.e(TAG, "handleSyncSignResult: failed/expired/declined")
                postJointSyncError()
            }
        }
    }

    private fun assembleAndPostSuccess(
        signRequest: SignRequestWithFullSignature,
        txList: List<TRANSACTION>
    ) {
        val transactionLists = signRequest.transactionLists
        val jointAccount = signRequest.jointAccount
        Log.d(TAG, "assemble: groups=${transactionLists?.size}, unsignedTx=${txList.size}")
        if (transactionLists.isNullOrEmpty() || !hasRequiredJointAccountData(jointAccount)) {
            Log.e(TAG, "assemble: FAIL validation, jointNull=${jointAccount == null}")
            postJointSyncError()
            return
        }
        val account = jointAccount!!
        val allSignedBytes = assembleAllGroups(transactionLists, account) ?: return
        Log.d(TAG, "assemble: totalSigned=${allSignedBytes.size}, expected=${txList.size}")
        if (allSignedBytes.size != txList.size) {
            Log.e(TAG, "assemble: SIZE MISMATCH ${allSignedBytes.size} vs ${txList.size}")
            postJointSyncError()
            return
        }
        externalTransactionQueuingHelper.clearCachedData()
        onJointSignedTransactionsAssembled(txList, allSignedBytes)
        Log.d(TAG, "assemble: SUCCESS")
        _signResultFlow.value = ExternalTransactionSignResult.Success(
            txList,
            allSignedBytes.map { it as ByteArray? }
        )
    }

    private fun assembleAllGroups(
        transactionLists: List<TransactionListWithFullSignature>,
        account: JointAccount
    ): MutableList<ByteArray>? {
        val participantAddresses = account.participantAddresses!!
        val version = account.version!!
        val threshold = account.threshold!!
        val allSignedBytes = mutableListOf<ByteArray>()
        for ((gIdx, entry) in transactionLists.withIndex()) {
            val rawTxns = entry.rawTransactions
            val responses = entry.responses
            Log.d(TAG, "assembleGroup[$gIdx]: rawTxns=${rawTxns?.size}, responses=${responses?.size}")
            if (rawTxns == null || responses == null) {
                Log.e(TAG, "assembleGroup[$gIdx]: null data")
                postJointSyncError()
                return null
            }
            logGroupResponses(gIdx, responses)
            when (val r = multisigTransactionAssembler.assemble(
                rawTransactionsBase64 = rawTxns,
                participantAddresses = participantAddresses,
                version = version,
                threshold = threshold,
                responses = responses
            )) {
                is PeraResult.Success -> {
                    Log.d(TAG, "assembleGroup[$gIdx]: ok=${r.data.size}")
                    allSignedBytes.addAll(r.data)
                }

                is PeraResult.Error -> {
                    Log.e(TAG, "assembleGroup[$gIdx]: FAIL", r.exception)
                    postJointSyncError()
                    return null
                }
            }
        }
        return allSignedBytes
    }

    private fun logGroupResponses(
        groupIdx: Int,
        responses: List<ParticipantSignature>
    ) {
        for ((rIdx, resp) in responses.withIndex()) {
            Log.d(
                TAG,
                "resp[$groupIdx][$rIdx] addr=${resp.address.take(ADDR_LOG_LEN)}, " +
                    "sigs=${resp.signatures.map { it != null }}"
            )
        }
    }

    private fun hasRequiredJointAccountData(jointAccount: JointAccount?): Boolean {
        if (jointAccount == null) return false
        return jointAccount.participantAddresses != null &&
                jointAccount.version != null &&
                jointAccount.threshold != null
    }

    private fun postJointSyncError() {
        postResult(
            ExternalTransactionSignResult.Error.Defined(
                AnnotatedString(R.string.joint_account_feature_not_supported)
            )
        )
    }

    private fun handleSignError(transaction: ExternalTransaction) {
        onTransactionSigned(transaction, null)
    }

    protected open fun onTransactionSigned(transaction: ExternalTransaction, signedTransaction: ByteArray?) {
        externalTransactionQueuingHelper.cacheDequeuedItem(signedTransaction)
    }

    protected open fun onJointSignedTransactionsAssembled(
        transactions: List<TRANSACTION>,
        signedBytesList: List<ByteArray>
    ) = Unit

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
        syncSignRequestPollingManager.stopPolling()
        ledgerBleSearchManager.stop()
        externalTransactionQueuingHelper.clearCachedData()
        transaction = null
    }

    fun manualStopAllResources() {
        this.stopAllResources()
        currentScope.coroutineContext.cancelChildren()
        ledgerBleOperationManager.manualStopAllProcess()
    }

    companion object {
        private const val TAG = "JOINT_SIGN_DEBUG"
        private const val ADDR_LOG_LEN = 8
        private const val SIG_PREVIEW_LEN = 10
    }
}
