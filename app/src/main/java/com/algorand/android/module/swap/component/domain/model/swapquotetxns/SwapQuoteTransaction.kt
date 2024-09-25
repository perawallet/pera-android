/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.component.domain.model.swapquotetxns

import com.algorand.android.algosdk.component.flatten
import com.algorand.android.foundation.common.mapToNotNullableListOrNull

sealed class SwapQuoteTransaction {

    abstract val transactionGroupId: String?
    abstract val unsignedTransactions: List<UnsignedSwapSingleTransactionData>
    abstract val signedTransactions: MutableList<SignedSwapSingleTransactionData>

    abstract val isTransactionConfirmationNeed: Boolean
    open val delayAfterConfirmation: Long? = null

    fun insertSignedTransaction(index: Int, signedSwapSingleTransactionData: SignedSwapSingleTransactionData) {
        signedTransactions[index] = signedSwapSingleTransactionData
    }

    fun getSignedTransactionsByteArray(): ByteArray? {
        return if (signedTransactions.size == 1) {
            signedTransactions.first().signedTransactionMsgPack
        } else {
            val safeSignedTransactions = signedTransactions.mapToNotNullableListOrNull { it?.signedTransactionMsgPack }
            safeSignedTransactions?.flatten()
        }
    }

    fun getTransactionsThatNeedsToBeSigned(): List<UnsignedSwapSingleTransactionData> {
        return signedTransactions.mapIndexedNotNull { index, swapSingleTransactionData ->
            if (swapSingleTransactionData.signedTransactionMsgPack == null) {
                unsignedTransactions[index]
            } else {
                null
            }
        }
    }

    data class OptInTransaction(
        override val transactionGroupId: String?,
        override val unsignedTransactions: List<UnsignedSwapSingleTransactionData>,
        override val signedTransactions: MutableList<SignedSwapSingleTransactionData>
    ) : SwapQuoteTransaction() {

        override val isTransactionConfirmationNeed: Boolean = true

        override val delayAfterConfirmation: Long
            get() = OPT_IN_CONFIRMATION_DELAY

        companion object {
            private const val OPT_IN_CONFIRMATION_DELAY = 1000L // 1 Sec
        }
    }

    data class SwapTransaction(
        override val transactionGroupId: String?,
        override val unsignedTransactions: List<UnsignedSwapSingleTransactionData>,
        override val signedTransactions: MutableList<SignedSwapSingleTransactionData>
    ) : SwapQuoteTransaction() {
        override val isTransactionConfirmationNeed: Boolean = true

    }

    data class PeraFeeTransaction(
        override val transactionGroupId: String?,
        override val unsignedTransactions: List<UnsignedSwapSingleTransactionData>,
        override val signedTransactions: MutableList<SignedSwapSingleTransactionData>
    ) : SwapQuoteTransaction() {
        override val isTransactionConfirmationNeed: Boolean = false
    }

    data object InvalidTransaction : SwapQuoteTransaction() {
        override val transactionGroupId: String? = null
        override val unsignedTransactions: List<UnsignedSwapSingleTransactionData> = emptyList()
        override val signedTransactions: MutableList<SignedSwapSingleTransactionData> = mutableListOf()
        override val isTransactionConfirmationNeed: Boolean = false
    }
}
