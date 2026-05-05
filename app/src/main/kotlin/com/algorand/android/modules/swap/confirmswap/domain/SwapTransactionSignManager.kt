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
import com.algorand.wallet.logger.PeraLogger
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
        val quoteTransactions = swapQuoteTransaction ?: return null
        val allRawBytes = quoteTransactions.flatMap { buildUserSigningRawBytes(it) ?: return null }
        return allRawBytes.takeIf { it.isNotEmpty() }?.let { listOf(it) }
    }

    private fun buildUserSigningRawBytes(group: SwapQuoteTransaction): List<ByteArray>? {
        return group.signedTransactions.mapIndexedNotNull { i, signed ->
            if (signed.signedTransactionMsgPack != null) return@mapIndexedNotNull null
            group.unsignedTransactions.getOrNull(i)?.transactionMsgPack?.decodeBase64()
                ?: return null
        }
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

    @Suppress("ReturnCount")
    override fun getSwapMetadataForService(): SwapServiceMetadata? {
        val swapId = pendingSwapIdForSyncService.takeIf { it != SwapServiceMetadata.INVALID_SWAP_ID }
        if (swapId == null) {
            PeraLogger.e(TAG, "getSwapMetadataForService: INVALID_SWAP_ID")
            return null
        }
        val quotes = swapQuoteTransaction
        if (quotes == null) {
            PeraLogger.e(TAG, "getSwapMetadataForService: quotes is null")
            return null
        }

        val types = mutableListOf<String>()
        val preSignedPerGroup = mutableListOf<List<ByteArray?>>()
        val unsignedCountPerGroup = mutableListOf<Int>()

        for ((idx, quote) in quotes.withIndex()) {
            val userRawBytes = buildUserSigningRawBytes(quote)
            if (userRawBytes == null) {
                PeraLogger.e(TAG, "getSwapMetadataForService: buildUserSigningRawBytes returned null at idx=$idx")
                return null
            }
            if (userRawBytes.isEmpty()) {
                continue
            }

            val typeName = quote.toTxnTypeName()
            if (typeName == null) {
                PeraLogger.e(TAG, "getSwapMetadataForService: unknown type at idx=$idx")
                return null
            }
            types.add(typeName)
            unsignedCountPerGroup.add(userRawBytes.size)

            val slots = quote.signedTransactions.map { it.signedTransactionMsgPack }
            val nullSlotCount = slots.count { it == null }
            if (nullSlotCount != userRawBytes.size) {
                PeraLogger.e(
                    TAG,
                    "getSwapMetadataForService: MISMATCH nullSlots=$nullSlotCount != userRawBytes=${userRawBytes.size}"
                )
                return null
            }
            preSignedPerGroup.add(slots)
        }

        if (types.isEmpty()) {
            PeraLogger.e(TAG, "getSwapMetadataForService: types is empty after processing")
            return null
        }
        return SwapServiceMetadata(swapId, types, preSignedPerGroup, unsignedCountPerGroup)
    }

    companion object {
        private const val TAG = "SwapTxnSignManager"
    }

    private fun SwapQuoteTransaction.toTxnTypeName(): String? = when (this) {
        is SwapQuoteTransaction.OptInTransaction -> SwapServiceMetadata.TXN_TYPE_OPTIN
        is SwapQuoteTransaction.SwapTransaction -> SwapServiceMetadata.TXN_TYPE_SWAP
        is SwapQuoteTransaction.PeraFeeTransaction -> SwapServiceMetadata.TXN_TYPE_PERA_FEE
        is SwapQuoteTransaction.InvalidTransaction -> null
    }
}
