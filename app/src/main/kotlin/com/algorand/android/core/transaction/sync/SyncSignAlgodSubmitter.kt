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

package com.algorand.android.core.transaction.sync

import com.algorand.android.core.transaction.external.SwapServiceMetadata
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.usecase.SendSignedTransactionUseCase
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.flatten
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.swap.domain.model.SignedSwapTransaction
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import com.algorand.wallet.swap.domain.usecase.SendSwapTransactions
import com.algorand.wallet.swap.domain.usecase.SetSwapStatusFailed
import com.algorand.wallet.transaction.domain.model.SignedTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SyncSignAlgodSubmitter @Inject constructor(
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val sendSwapTransactions: SendSwapTransactions,
    private val setSwapStatusFailed: SetSwapStatusFailed,
    private val errorLogger: PeraErrorLogger
) {

    suspend fun submit(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): String? = withContext(Dispatchers.IO) {
        when (session.algodSubmissionKind) {
            JointSyncAlgodSubmissionKind.NONE -> null
            JointSyncAlgodSubmissionKind.SWAP -> submitSwap(session, assembledGroups)
            JointSyncAlgodSubmissionKind.ARC59_SEND,
            JointSyncAlgodSubmissionKind.ARC59_CLAIM -> submitArc59(session, assembledGroups)
        }
    }

    private suspend fun submitSwap(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): String? {
        if (session.swapId == SwapServiceMetadata.INVALID_SWAP_ID) return null
        val flattenedPerGroup = assembledGroups.map { it.flatten() }
        val signedSwapTxns = buildSignedSwapTransactions(session.swapTxnTypes, flattenedPerGroup)
        if (signedSwapTxns == null) {
            errorLogger.logError("SyncSign: Swap txn type/count mismatch")
            setSwapStatusFailed(session.swapId, SwapStatusFailureReason.OTHER)
            return null
        }
        return when (val result = sendSwapTransactions(session.swapId, signedSwapTxns)) {
            is PeraResult.Success -> result.data.firstOrNull()?.value
            is PeraResult.Error -> {
                errorLogger.logError(
                    IllegalStateException(
                        "SyncSign: Swap send failed for swapId=${session.swapId}",
                        result.exception
                    )
                )
                setSwapStatusFailed(session.swapId, SwapStatusFailureReason.OTHER)
                null
            }
        }
    }

    private suspend fun submitArc59(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): String? {
        val flattened = assembledGroups.flatMap { it }.flatten()
        val detail: SignedTransactionDetail = when (session.algodSubmissionKind) {
            JointSyncAlgodSubmissionKind.ARC59_SEND -> SignedTransactionDetail.Arc59Send(flattened)
            JointSyncAlgodSubmissionKind.ARC59_CLAIM -> SignedTransactionDetail.Arc59ClaimOrReject(flattened)
            else -> return null
        }
        var txnId: String? = null
        sendSignedTransactionUseCase.sendSignedTransaction(detail).collect { resource ->
            when (resource) {
                is DataResource.Success -> {
                    txnId = resource.data.takeIf { it.isNotBlank() }
                }

                is DataResource.Error -> {
                    val ex = resource.exception
                        ?: IllegalStateException("SyncSign: ARC59 send failed for ${session.signRequestId}")
                    errorLogger.logError(ex)
                    txnId = null
                }

                else -> Unit
            }
        }
        return txnId
    }

    private fun buildSignedSwapTransactions(
        types: List<String>,
        allSignedBytes: List<ByteArray>
    ): List<SignedSwapTransaction>? {
        if (types.size != allSignedBytes.size) return null
        return types.zip(allSignedBytes).map { (typeName, bytes) ->
            val type = parseSwapTxnType(typeName) ?: return null
            SignedSwapTransaction(signedTransaction = SignedTransaction(bytes), type = type)
        }
    }

    private fun parseSwapTxnType(name: String): SignedSwapTransaction.Type? {
        return when (name) {
            SwapServiceMetadata.TXN_TYPE_OPTIN -> SignedSwapTransaction.Type.OptIn
            SwapServiceMetadata.TXN_TYPE_SWAP -> SignedSwapTransaction.Type.Swap
            SwapServiceMetadata.TXN_TYPE_PERA_FEE -> SignedSwapTransaction.Type.PeraFee
            else -> null
        }
    }
}
