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

package com.algorand.android.ui.swap.confirmation.mapper

import com.algorand.android.modules.swap.confirmswap.domain.model.SwapQuoteTransaction
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.wallet.swap.domain.model.SignedSwapTransaction
import com.algorand.wallet.transaction.domain.model.SignedTransaction
import javax.inject.Inject

internal class DefaultSignedSwapTransactionMapper @Inject constructor() : SignedSwapTransactionMapper {

    override fun invoke(result: ExternalTransactionSignResult.Success<*>): List<SignedSwapTransaction>? {
        val signedTxns = result.signedTransaction as? List<SwapQuoteTransaction> ?: return null
        return signedTxns.map { swapTransaction ->
            val txnByteArrays = swapTransaction.getSignedTransactionsByteArray() ?: return null
            val type = getSwapType(swapTransaction) ?: return null
            SignedSwapTransaction(
                signedTransaction = SignedTransaction(txnByteArrays),
                type = type
            )
        }
    }

    private fun getSwapType(swapTransaction: SwapQuoteTransaction): SignedSwapTransaction.Type? {
        return when (swapTransaction) {
            is SwapQuoteTransaction.SwapTransaction -> SignedSwapTransaction.Type.Swap
            is SwapQuoteTransaction.OptInTransaction -> SignedSwapTransaction.Type.OptIn
            is SwapQuoteTransaction.PeraFeeTransaction -> SignedSwapTransaction.Type.PeraFee
            SwapQuoteTransaction.InvalidTransaction -> null
        }
    }
}
