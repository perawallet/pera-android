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

package com.algorand.android.core.transaction

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.coroutineScope
import com.algorand.algosdk.sdk.BytesArray
import com.algorand.android.R
import com.algorand.android.ledger.CustomScanCallback
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.TransactionSignOperation
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.Arc59TransactionData
import com.algorand.android.models.LedgerBleResult
import com.algorand.android.models.Result
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionManagerResult
import com.algorand.android.models.TransactionManagerResult.Error.GlobalWarningError.Defined
import com.algorand.android.models.TransactionManagerResult.Error.GlobalWarningError.MinBalanceError
import com.algorand.android.models.TransactionParams
import com.algorand.android.models.TransactionSignData
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.utils.Event
import com.algorand.android.utils.LifecycleScopedCoroutineOwner
import com.algorand.android.utils.ListQueuingHelper
import com.algorand.android.utils.TransactionSignSigningHelper
import com.algorand.android.utils.assignGroupId
import com.algorand.android.utils.flatten
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.getReceiverMinBalanceFee
import com.algorand.android.utils.getTxFee
import com.algorand.android.utils.isLesserThan
import com.algorand.android.utils.makeAddAssetTx
import com.algorand.android.utils.makeArc59Txn
import com.algorand.android.utils.makeRekeyTx
import com.algorand.android.utils.makeRemoveAssetTx
import com.algorand.android.utils.makeSendAndRemoveAssetTx
import com.algorand.android.utils.makeTx
import com.algorand.android.utils.mapToNotNullableListOrNull
import com.algorand.android.utils.minBalancePerAssetAsBigInteger
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.signTx
import com.algorand.android.utils.toBytesArray
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalance
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingAmount
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigInteger
import java.net.ConnectException
import java.net.SocketException
import javax.inject.Inject
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class TransactionSignManager @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val transactionsRepository: TransactionsRepository,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val signHelper: TransactionSignSigningHelper,
    private val getAccountAlgoBalance: GetAccountAlgoBalance,
    private val getAccountAssetHoldingAmount: GetAccountAssetHoldingAmount,
    private val getAccountMinBalance: GetAccountMinBalance,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val getLocalAccount: GetLocalAccount,
    private val signHdKeyTransaction: SignHdKeyTransaction
) : LifecycleScopedCoroutineOwner() {

    val transactionManagerResultLiveData = MutableLiveData<Event<TransactionManagerResult>?>()

    private var transactionParams: TransactionParams? = null
    var transactionDataList: List<TransactionSignData>? = null

    private val scanCallback = object : CustomScanCallback() {
        override fun onLedgerScanned(
            device: BluetoothDevice,
            currentTransactionIndex: Int?,
            totalTransactionCount: Int?
        ) {
            ledgerBleSearchManager.stop()
            currentScope.launch {
                signHelper.currentItem?.run {
                    ledgerBleOperationManager.startLedgerOperation(
                        newOperation = TransactionSignOperation(device, this),
                        currentTransactionIndex = currentTransactionIndex,
                        totalTransactionCount = totalTransactionCount
                    )
                }
            }
        }

        override fun onScanError(errorMessageResId: Int, titleResId: Int) {
            setSignFailed(TransactionManagerResult.LedgerScanFailed)
        }
    }

    private val operationManagerCollectorAction: (suspend (Event<LedgerBleResult>?) -> Unit) = { ledgerBleResultEvent ->
        ledgerBleResultEvent?.consume()?.run {
            when (this) {
                is LedgerBleResult.LedgerWaitingForApproval -> {
                    postResult(TransactionManagerResult.LedgerWaitingForApproval(bluetoothName))
                }
                is LedgerBleResult.SignedTransactionResult -> checkAndCacheSignedTransaction(transactionByteArray)
                is LedgerBleResult.LedgerErrorResult -> {
                    setSignFailed(TransactionManagerResult.Error.GlobalWarningError.Api(errorMessage))
                }
                is LedgerBleResult.AppErrorResult -> setSignFailed(Defined(AnnotatedString(errorMessageId), titleResId))
                is LedgerBleResult.OperationCancelledResult -> setSignFailed(
                    Defined(AnnotatedString(R.string.error_cancelled_message), R.string.error_cancelled_title)
                )
                is LedgerBleResult.OnMissingBytes -> setSignFailed(
                    Defined(AnnotatedString(R.string.error_sending_message), R.string.error_bluetooth_title)
                )
                else -> sendErrorLog("Unhandled else case in operationManagerCollectorAction")
            }
        }
    }

    private val signHelperListener = object : ListQueuingHelper.Listener<TransactionSignData, ByteArray> {
        override fun onAllItemsDequeued(signedTransactions: List<ByteArray?>) {
            if (signedTransactions.isEmpty() || signedTransactions.any { it == null }) {
                setSignFailed(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
                return
            }
            if (signedTransactions.size == 1) {
                transactionDataList?.let { postTxnSignResult(signedTransactions.firstOrNull(), it.firstOrNull()) }
            } else {
                val safeSignedTransactions = signedTransactions.mapToNotNullableListOrNull { it }
                if (safeSignedTransactions == null) {
                    postResult(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
                    return
                }
                transactionDataList?.let { postGroupTxnSignResult(safeSignedTransactions, it) }
            }
        }

        override fun onNextItemToBeDequeued(
            transaction: TransactionSignData,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            // TODO: add [currentItemIndex] and [totalItemCount] after merging this core swap screens
            currentScope.launch {
                transaction.signTxn()
            }
        }
    }

    private suspend fun checkAndCacheSignedTransaction(transactionByteArray: ByteArray?) {
        if (transactionByteArray == null) {
            setSignFailed(Defined(AnnotatedString(R.string.unknown_error)))
            return
        }
        signHelper.currentItem?.run {
            if (isArc59Transaction) {
                return@run
            }
            calculatedFee = transactionParams?.getTxFee(transactionByteArray)
            if (this is TransactionSignData.Send && projectedFee != calculatedFee) {
                currentScope.launch { resignCurrentTransaction() }
                return
            }

            if (isMinimumLimitViolated()) {
                return
            }
        }
        signHelper.cacheDequeuedItem(transactionByteArray)
    }

    private fun setSignFailed(transactionManagerResult: TransactionManagerResult) {
        postResult(transactionManagerResult)
        signHelper.clearCachedData()
    }

    private suspend fun resignCurrentTransaction() {
        signHelper.currentItem?.createTransaction()
        signHelper.requeueCurrentItem()
    }

    fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        setupLedgerOperationManager(lifecycle)
        signHelper.initListener(signHelperListener)
    }

    private fun setupLedgerOperationManager(lifecycle: Lifecycle) {
        ledgerBleOperationManager.setup(lifecycle)
        lifecycle.coroutineScope.launch {
            ledgerBleOperationManager.ledgerBleResultFlow.collect {
                operationManagerCollectorAction.invoke(it)
            }
        }
    }

    fun initSigningTransactions(isGroupTransaction: Boolean, vararg transactionData: TransactionSignData) {
        currentScope.launch {
            postResult(TransactionManagerResult.Loading)
            transactionData.toList().ifEmpty {
                setSignFailed(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
                return@launch
            }.let { transactionList ->
                processTransactionDataList(transactionList, isGroupTransaction)?.let {
                    this@TransactionSignManager.transactionDataList = it
                    signHelper.initItemsToBeEnqueued(it)
                }
            }
        }
    }

    suspend fun createArc59SendTransactionList(transactionData: TransactionSignData): List<Arc59TransactionData>? {
        return transactionData.createArc59SendTransactions()
    }

    suspend fun getReceiverMinBalanceFee(transactionData: TransactionSignData): Long? {
        val transactionParams = getTransactionParams(transactionData) ?: return null
        this@TransactionSignManager.transactionParams = transactionParams

        val sendTransaction = transactionData as? TransactionSignData.Send ?: return null
        val receiverAlgoAmount = sendTransaction.targetUser.algoBalance ?: return null
        val receiverMinBalanceAmount = sendTransaction.targetUser.minBalance ?: return null

        return getReceiverMinBalanceFee(
            receiverAlgoAmount = receiverAlgoAmount,
            receiverMinBalanceAmount = receiverMinBalanceAmount
        )
    }

    private suspend fun TransactionSignData.signTxn() {
        when (signer) {
            is TransactionSigner.Algo25 -> {
                val secretKey = getAlgo25SecretKey(signer.address) ?: run {
                    setSignFailed(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
                    return
                }
                checkAndCacheSignedTransaction(transactionByteArray?.signTx(secretKey))
            }
            is TransactionSigner.HdKey -> {
                val transactionBytes = transactionByteArray ?: return handleSignError()
                val hdKey = getLocalAccount(signer.address) as? LocalAccount.HdKey ?: return handleSignError()
                val seed = getHdSeed(seedId = hdKey.seedId) ?: return handleSignError()

                val transactionSignedByteArray = signHdKeyTransaction.signTransaction(
                    transactionBytes, seed, hdKey.account, hdKey.change, hdKey.keyIndex
                ) ?: return handleSignError()

                checkAndCacheSignedTransaction(transactionSignedByteArray)
            }
            is TransactionSigner.LedgerBle -> sendTransactionWithLedger(signer as TransactionSigner.LedgerBle)
            is TransactionSigner.SignerNotFound -> {
                postResult(Defined(AnnotatedString(stringResId = R.string.the_signing_account_has)))
            }
        }
    }

    private fun handleSignError() {
        setSignFailed(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
    }

    private suspend fun TransactionSignData.createArc59SendTransactions(): List<Arc59TransactionData>? {
        val transactionParams = getTransactionParams(this) ?: return null
        this@TransactionSignManager.transactionParams = transactionParams
        val arc59TransactionData = mutableListOf<Arc59TransactionData>()
        (this as? TransactionSignData.Send)?.let {
            projectedFee = calculatedFee ?: transactionParams.getTxFee()
            val transactions = transactionParams.makeArc59Txn(
                senderAddress = senderAccountAddress,
                receiverAddress = targetUser.publicKey,
                transactionAmount = amount,
                senderAlgoAmount = senderAlgoAmount,
                senderMinBalanceAmount = minimumBalance.toBigInteger(),
                receiverAlgoAmount = targetUser.algoBalance ?: BigInteger.ZERO,
                receiverMinBalanceAmount = targetUser.minBalance ?: BigInteger.ZERO,
                assetId = assetId,
                note = if (xnote.isNullOrBlank()) note else xnote
            )

            for (i in 0 until transactions.length()) {
                val txn = transactions.getTxn(i)
                val signer = transactions.getSigner(i)
                arc59TransactionData.add(Arc59TransactionData(txn, signer))
            }
        }
        return arc59TransactionData
    }

    @SuppressWarnings("LongMethod")
    suspend fun TransactionSignData.createTransaction(): ByteArray? {
        val transactionParams = getTransactionParams(this) ?: return null
        this@TransactionSignManager.transactionParams = transactionParams

        val createdTransactionByteArray = when (this) {
            is TransactionSignData.Send -> {
                projectedFee = calculatedFee ?: transactionParams.getTxFee()
                // calculate isMax before calculating real amount because while isMax true fee will be deducted.
                isMax = isTransactionMax(amount, senderAccountAddress, assetId)
                // TODO: 10.08.2022 Get all those calculations from a single AmountTransactionValidationUseCase
                amount = calculateAmount(
                    projectedAmount = amount,
                    isMax = isMax,
                    isSenderRekeyedToAnotherAccount = isSenderRekeyed(),
                    senderMinimumBalance = minimumBalance,
                    assetId = assetId,
                    fee = projectedFee
                ) ?: return null

                if (isSenderRekeyed()) {
                    // if account is rekeyed to another account, min balance should be deducted from the amount.
                    // after it'll be deducted, isMax will be false to not write closeToAddress.
                    isMax = false
                }

                if (isCloseToSameAccount()) {
                    return null
                }

                transactionParams.makeTx(
                    senderAddress = senderAccountAddress,
                    receiverAddress = targetUser.publicKey,
                    amount = amount,
                    assetId = assetId,
                    isMax = isMax,
                    note = if (xnote.isNullOrBlank()) note else xnote
                )
            }
            is TransactionSignData.AddAsset -> {
                transactionParams.makeAddAssetTx(senderAccountAddress, assetId)
            }
            is TransactionSignData.RemoveAsset -> {
                if (shouldCreateAssetRemoveTransaction(senderAccountAddress, assetId)) {
                    transactionParams.makeRemoveAssetTx(
                        senderAddress = senderAccountAddress,
                        creatorPublicKey = creatorAddress,
                        assetId = assetId
                    )
                } else {
                    null
                }
            }
            is TransactionSignData.SendAndRemoveAsset -> {
                transactionParams.makeSendAndRemoveAssetTx(
                    senderAddress = senderAccountAddress,
                    receiverAddress = targetUser.publicKey,
                    assetId = assetId,
                    amount = amount
                )
            }
            is TransactionSignData.Rekey -> {
                transactionParams.makeRekeyTx(senderAccountAddress, rekeyAdminAddress)
            }
        }

        transactionByteArray = createdTransactionByteArray

        return createdTransactionByteArray
    }

    private suspend fun getTransactionParams(transactionData: TransactionSignData): TransactionParams? {
        when (val result = transactionsRepository.getTransactionParams()) {
            is Result.Success -> {
                transactionParams = result.data
            }
            is Result.Error -> {
                transactionParams = null
                when (result.exception.cause) {
                    is ConnectException, is SocketException -> {
                        postResult(Defined(AnnotatedString(R.string.the_internet_connection)))
                    }
                    else -> {
                        when (transactionData) {
                            is TransactionSignData.AddAsset -> {
                                postResult(
                                    TransactionManagerResult.Error.SnackbarError.Retry(
                                        titleResId = R.string.error_while_opting_to_the,
                                        descriptionResId = null,
                                        buttonTextResId = R.string.retry
                                    )
                                )
                            }
                            is TransactionSignData.Rekey,
                            is TransactionSignData.Send,
                            is TransactionSignData.SendAndRemoveAsset,
                            is TransactionSignData.RemoveAsset -> {
                                postResult(
                                    TransactionManagerResult.Error.GlobalWarningError.Api(
                                        result.exception.message.orEmpty()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        return transactionParams
    }

    private fun sendCurrentTransaction(bluetoothDevice: BluetoothDevice) {
        signHelper.currentItem?.run {
            ledgerBleOperationManager.startLedgerOperation(TransactionSignOperation(bluetoothDevice, this))
        }
    }

    private fun calculateAmount(
        projectedAmount: BigInteger,
        isMax: Boolean,
        isSenderRekeyedToAnotherAccount: Boolean,
        senderMinimumBalance: Long,
        assetId: Long,
        fee: Long
    ): BigInteger? {
        val calculatedAmount = if (isMax && assetId == ALGO_ID) {
            if (isSenderRekeyedToAnotherAccount) {
                projectedAmount - fee.toBigInteger() - senderMinimumBalance.toBigInteger()
            } else {
                projectedAmount - fee.toBigInteger()
            }
        } else {
            projectedAmount
        }

        if (calculatedAmount isLesserThan BigInteger.ZERO) {
            if (isSenderRekeyedToAnotherAccount) {
                val errorMinBalance = AnnotatedString(
                    stringResId = R.string.the_transaction_cannot_be,
                    replacementList = listOf("min_balance" to senderMinimumBalance.formatAsAlgoString())
                )
                postResult(Defined(errorMinBalance))
            } else {
                postResult(Defined(AnnotatedString(R.string.transaction_amount_results)))
            }
            return null
        }

        return calculatedAmount
    }

    private suspend fun isTransactionMax(amount: BigInteger, publicKey: String, assetId: Long): Boolean {
        return if (assetId != ALGO_ID) {
            false
        } else {
            getAccountAlgoBalance(publicKey) == amount
        }
    }

    private suspend fun shouldCreateAssetRemoveTransaction(publicKey: String, assetId: Long): Boolean {
        val assetHoldingAmount = getAccountAssetHoldingAmount(publicKey, assetId)
        return assetHoldingAmount != null && assetHoldingAmount == BigInteger.ZERO
    }

    private fun TransactionSignData.isCloseToSameAccount(): Boolean {
        if (this is TransactionSignData.Send && isMax && senderAccountAddress == targetUser.publicKey) {
            postResult(Defined(AnnotatedString(R.string.you_can_not_send_your)))
            return true
        }
        return false
    }

    private suspend fun TransactionSignData.isMinimumLimitViolated(): Boolean {
        if (this is TransactionSignData.Send && isMax) {
            return false
        }

        // every asset addition increases min balance by $MIN_BALANCE_PER_ASSET
        var minBalance = getAccountMinBalance(senderAccountAddress)
        when (this) {
            is TransactionSignData.AddAsset ->
                minBalance += minBalancePerAssetAsBigInteger
            is TransactionSignData.RemoveAsset -> {
                minBalance -= minBalancePerAssetAsBigInteger
            }
            else -> {
                sendErrorLog("Unhandled else case in isMinimumLimitViolated")
            }
        }

        val balance = getAccountAlgoBalance(senderAccountAddress) ?: run {
            setSignFailed(Defined(AnnotatedString(stringResId = R.string.minimum_balance_required)))
            return true
        }

        val fee = calculatedFee?.toBigInteger() ?: run {
            setSignFailed(Defined(AnnotatedString(stringResId = R.string.minimum_balance_required)))
            return true
        }

        // fee only drops from the algos.
        val balanceAfterTransaction =
            if (this is TransactionSignData.Send && assetId != ALGO_ID) {
                balance - fee
            } else {
                balance - fee - amount
            }

        if (balanceAfterTransaction < minBalance) {
            if (this is TransactionSignData.AddAsset) {
                postResult(MinBalanceError(minBalance + fee))
            } else {
                val description = AnnotatedString(
                    stringResId = R.string.transaction_amount,
                    replacementList = listOf("min_balance" to minBalance.formatAsAlgoString())
                )
                postResult(Defined(description))
            }
            return true
        }

        return false
    }

    private fun postResult(transactionManagerResult: TransactionManagerResult) {
        transactionManagerResultLiveData.postValue(Event(transactionManagerResult))
    }

    private fun sendTransactionWithLedger(ledgerDetail: TransactionSigner.LedgerBle) {
        val bluetoothAddress = ledgerDetail.bluetoothAddress
        val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
        if (currentConnectedDevice != null && currentConnectedDevice.address == bluetoothAddress) {
            sendCurrentTransaction(currentConnectedDevice)
        } else {
            searchForDevice(bluetoothAddress)
        }
    }

    private fun searchForDevice(ledgerAddress: String) {
        ledgerBleSearchManager.scan(
            newScanCallback = scanCallback,
            filteredAddress = ledgerAddress,
            coroutineScope = currentScope
        )
    }

    // this also stops LedgerBleOperationManager.
    fun manualStopAllResources() {
        this.stopAllResources()
        currentScope.coroutineContext.cancelChildren()
        ledgerBleOperationManager.manualStopAllProcess()
    }

    override fun stopAllResources() {
        ledgerBleSearchManager.stop()
        transactionManagerResultLiveData.value = null
        transactionDataList = null
    }

    private suspend fun processTransactionDataList(
        transactionDataList: List<TransactionSignData>,
        isGroupTransaction: Boolean
    ): List<TransactionSignData>? {
        for (transactionData in transactionDataList) {
            transactionData.transactionByteArray ?: transactionData.createTransaction() ?: return null
        }

        if (isGroupTransaction) {
            createGroupedBytesArray(transactionDataList)?.let {
                for (index in 0L until it.length()) {
                    transactionDataList[index.toInt()].transactionByteArray = it.get(index)
                }
            }
        }
        return transactionDataList
    }

    private fun postTxnSignResult(
        bytesArray: ByteArray?,
        transactionData: TransactionSignData?
    ) {
        if (bytesArray == null || transactionData == null) {
            postResult(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
        } else {
            postResult(TransactionManagerResult.Success(transactionData.getSignedTransactionDetail(bytesArray)))
        }
    }

    private fun postGroupTxnSignResult(
        groupedBytesArrayList: List<ByteArray>,
        transactionDataList: List<TransactionSignData>
    ) {
        val signedGroupTxnDetailList = createSignedTransactionDetailList(transactionDataList, groupedBytesArrayList)
        if (signedGroupTxnDetailList.isNotEmpty()) {
            postResult(
                TransactionManagerResult.Success(
                    SignedTransactionDetail.Group(
                        groupedBytesArrayList.flatten(),
                        signedGroupTxnDetailList
                    )
                )
            )
        } else {
            postResult(Defined(AnnotatedString(stringResId = R.string.an_error_occured)))
        }
    }

    private fun createSignedTransactionDetailList(
        transactionDataList: List<TransactionSignData>,
        signedBytesArrayList: List<ByteArray>
    ): List<SignedTransactionDetail> {
        return mutableListOf<SignedTransactionDetail>().apply {
            for (index in transactionDataList.indices) {
                val signedTxn = signedBytesArrayList[index]
                add(transactionDataList[index].getSignedTransactionDetail(signedTxn))
            }
        }
    }

    private fun createGroupedBytesArray(transactionDataList: List<TransactionSignData>): BytesArray? {
        return mutableListOf<ByteArray>().apply {
            transactionDataList.forEach {
                it.transactionByteArray?.let { transactionByteArray ->
                    add(transactionByteArray)
                } ?: return null
            }
        }.toBytesArray().assignGroupId()
    }
}
