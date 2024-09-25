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

package com.algorand.android.module.swap.component.data.mapper

import com.algorand.android.module.swap.component.data.model.SwapQuoteTransactionResponse
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransactionResult
import com.algorand.android.module.swap.component.domain.model.SwapTransactionPurpose
import com.algorand.android.module.swap.component.domain.model.SwapTransactionPurpose.Companion.fallbackTransactionPurpose
import javax.inject.Inject

internal class SwapQuoteTransactionMapperImpl @Inject constructor() : SwapQuoteTransactionMapper {

    override fun invoke(response: SwapQuoteTransactionResponse): SwapQuoteTransactionResult {
        val purpose = SwapTransactionPurpose.entries.firstOrNull {
            it.value == response.purpose
        } ?: fallbackTransactionPurpose
        return SwapQuoteTransactionResult(
            purpose = purpose,
            transactionGroupId = response.transactionGroupId,
            transactions = response.transactions,
            signedTransactions = response.signedTransactions
        )
    }
}
