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

package com.algorand.android.modules.swap.confirmswap.domain

import com.algorand.android.core.transaction.external.ExternalTransactionSignManager
import com.algorand.android.core.transaction.external.SwapServiceMetadata
import com.algorand.android.core.transaction.sync.JointAccountSyncSignDependencies
import com.algorand.android.core.transaction.sync.JointSyncAlgodSubmissionKind
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.ExternalTransaction
import com.algorand.android.modules.swap.confirmswap.domain.model.SignedSwapSingleTransactionData
import com.algorand.android.modules.swap.confirmswap.domain.model.SwapQuoteTransaction
import com.algorand.android.modules.swap.confirmswap.domain.model.UnsignedSwapSingleTransactionData
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionQueuingHelper
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.utils.decodeBase64
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SwapTransactionSignManager @Inject constructor(
    ledgerBleSearchManager: LedgerBleSearchManager,
    ledgerBleOperationManager: LedgerBleOperationManager,
    externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    getTransactionSigner: GetTransactionSigner,
    getAlgo25SecretKey: GetAlgo25SecretKey,
    getHdSeed: GetHdSeed,
    getLocalAccount: GetLocalAccount,
    signHdKeyTransaction: SignHdKeyTransaction,
    syncSignDependencies: JointAccountSyncSignDependencies
) : ExternalTransactionSignManager<UnsignedSwapSingleTransactionData>(
    ledgerBleSearchManager,
    ledgerBleOperationManager,
    externalTransactionQueuingHelper,
    getTransactionSigner,
    getAlgo25SecretKey,
    getHdSeed,
    getLocalAccount,
    signHdKeyTransaction,
    syncSignDependencies
) {

    val swapTransactionSignResultFlow: Flow<ExternalTransactionSignResult> = signResultFlow.map {
        when (it) {
            is ExternalTransactionSignResult.Success<*> -> {
                swapQuoteTransaction?.run {
                    ExternalTransactionSignResult.Success(
                        signedTransaction = this,
                        signedTransactionsByteArray = it.signedTransactionsByteArray,
                        algodTransactionIdIfAlreadySubmitted = it.algodTransactionIdIfAlreadySubmitted
                    )
                } ?: it
            }

            else -> it
        }
    }

    private var swapQuoteTransaction: List<SwapQuoteTransaction>? = null
    private var pendingSwapIdForSyncService: Long = SwapServiceMetadata.INVALID_SWAP_ID

    fun signSwapQuoteTransaction(swapQuoteTransaction: List<SwapQuoteTransaction>, swapId: Long) {
        this.swapQuoteTransaction = swapQuoteTransaction
        this.pendingSwapIdForSyncService = swapId
        val unsignedTransactionList = swapQuoteTransaction.map { it.getTransactionsThatNeedsToBeSigned() }.flatten()
        signTransaction(unsignedTransactionList)
    }

    override fun onTransactionSigned(transaction: ExternalTransaction, signedTransaction: ByteArray?) {
        insertSignedSwapTransaction(transaction, signedTransaction)
        super.onTransactionSigned(transaction, signedTransaction)
    }

    override fun buildRawBytesGroups(
        txList: List<UnsignedSwapSingleTransactionData>
    ): List<List<ByteArray>>? {
        val quoteTransactions = swapQuoteTransaction
        if (quoteTransactions == null) {
            return null
        }
        val result = mutableListOf<List<ByteArray>>()
        for (group in quoteTransactions) {
            val allTxns = group.unsignedTransactions
            val allRawBytes = mutableListOf<ByteArray>()
            for (txData in allTxns) {
                val bytes = txData.transactionMsgPack?.decodeBase64()
                if (bytes == null) {
                    return null
                }
                allRawBytes.add(bytes)
            }
            if (allRawBytes.isEmpty()) {
                continue
            }
            result.add(allRawBytes)
        }
        if (result.isEmpty()) {
            return null
        }
        return result
    }

    private fun insertSignedSwapTransaction(transaction: ExternalTransaction, signedTransaction: ByteArray?) {
        (transaction as? UnsignedSwapSingleTransactionData)?.run {
            val signedSingleTransactionData = SignedSwapSingleTransactionData(
                parentListIndex,
                transactionListIndex,
                signedTransaction
            )
            swapQuoteTransaction?.get(signedSingleTransactionData.parentListIndex)?.insertSignedTransaction(
                transactionListIndex,
                signedSingleTransactionData
            )
        }
    }

    override fun jointSyncAlgodSubmissionKind(): JointSyncAlgodSubmissionKind =
        JointSyncAlgodSubmissionKind.SWAP

    override fun getSwapMetadataForService(): SwapServiceMetadata? {
        val swapId = pendingSwapIdForSyncService
        val quotes = swapQuoteTransaction ?: return null
        if (swapId == SwapServiceMetadata.INVALID_SWAP_ID) return null
        val types = quotes.map { quote ->
            when (quote) {
                is SwapQuoteTransaction.OptInTransaction -> SwapServiceMetadata.TXN_TYPE_OPTIN
                is SwapQuoteTransaction.SwapTransaction -> SwapServiceMetadata.TXN_TYPE_SWAP
                is SwapQuoteTransaction.PeraFeeTransaction -> SwapServiceMetadata.TXN_TYPE_PERA_FEE
                is SwapQuoteTransaction.InvalidTransaction -> return null
            }
        }
        return SwapServiceMetadata(swapId = swapId, txnTypes = types)
    }
}
