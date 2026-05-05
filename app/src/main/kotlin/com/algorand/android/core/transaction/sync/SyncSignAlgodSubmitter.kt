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
import com.algorand.wallet.logger.PeraLogger
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
    private val setSwapStatusFailed: SetSwapStatusFailed
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
        if (session.swapId == SwapServiceMetadata.INVALID_SWAP_ID) {
            PeraLogger.e(TAG, "submitSwap: INVALID_SWAP_ID")
            return null
        }

        val signedSwapTxns = buildSignedSwapTransactions(session, assembledGroups)
        if (signedSwapTxns == null) {
            PeraLogger.e(TAG, "submitSwap: buildSignedSwapTransactions returned null, marking swap failed")
            setSwapStatusFailed(session.swapId, SwapStatusFailureReason.OTHER)
            return null
        }

        return when (val result = sendSwapTransactions(session.swapId, signedSwapTxns)) {
            is PeraResult.Success -> result.data.firstOrNull()?.value
            is PeraResult.Error -> {
                setSwapStatusFailed(session.swapId, SwapStatusFailureReason.OTHER)
                null
            }
        }
    }

    private fun buildSignedSwapTransactions(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): List<SignedSwapTransaction>? {
        val allAssembled = assembledGroups.flatMap { it }
        val unsignedCounts = session.swapUnsignedCountPerGroup
        val slotBytesPerGroup = session.swapPreSignedBytesPerGroup
        val types = session.swapTxnTypes

        if (types.isEmpty() || unsignedCounts.size != types.size) {
            PeraLogger.e(
                TAG, "buildSignedSwapTransactions: FAIL types/counts mismatch " +
                        "types.size=${types.size} unsignedCounts.size=${unsignedCounts.size}"
            )
            return null
        }
        if (allAssembled.size != unsignedCounts.sum()) {
            PeraLogger.e(
                TAG, "buildSignedSwapTransactions: FAIL assembled count mismatch " +
                        "allAssembled=${allAssembled.size} unsignedCounts.sum=${unsignedCounts.sum()}"
            )
            return null
        }

        var offset = 0
        return types.mapIndexed { index, typeName ->
            val type = parseSwapTxnType(typeName)
            if (type == null) {
                PeraLogger.e(TAG, "buildSignedSwapTransactions: FAIL unknown txnType=$typeName")
                return null
            }
            val count = unsignedCounts[index]
            val assembledSlice = allAssembled.subList(offset, offset + count)
            offset += count

            val slots = slotBytesPerGroup.getOrNull(index).orEmpty()
            val interleaved = interleaveWithAssembled(slots, assembledSlice)
            if (interleaved == null) {
                PeraLogger.e(
                    TAG, "buildSignedSwapTransactions: FAIL interleave at index=$index " +
                            "slots.size=${slots.size} assembledSlice.size=${assembledSlice.size} " +
                            "nullSlots=${slots.count { it == null }}"
                )
                return null
            }

            SignedSwapTransaction(
                signedTransaction = SignedTransaction(interleaved.flatten()),
                type = type
            )
        }
    }

    private fun interleaveWithAssembled(
        slots: List<ByteArray?>,
        assembled: List<ByteArray>
    ): List<ByteArray>? {
        var jointIdx = 0
        val result = slots.map { slot -> slot ?: assembled.getOrNull(jointIdx++) ?: return null }
        return if (jointIdx == assembled.size) result else null
    }

    private fun parseSwapTxnType(name: String): SignedSwapTransaction.Type? {
        return when (name) {
            SwapServiceMetadata.TXN_TYPE_OPTIN -> SignedSwapTransaction.Type.OptIn
            SwapServiceMetadata.TXN_TYPE_SWAP -> SignedSwapTransaction.Type.Swap
            SwapServiceMetadata.TXN_TYPE_PERA_FEE -> SignedSwapTransaction.Type.PeraFee
            else -> null
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
                    txnId = null
                }

                else -> Unit
            }
        }
        return txnId
    }

    companion object {
        private const val TAG = "SyncSignAlgodSubmitter"
    }
}
