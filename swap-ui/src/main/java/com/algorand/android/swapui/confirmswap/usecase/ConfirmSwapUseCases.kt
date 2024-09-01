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

package com.algorand.android.swapui.confirmswap.usecase

import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.swapui.confirmswap.model.ConfirmSwapPreview
import com.algorand.android.swapui.txnstatus.model.SwapTransactionStatusNavArgs

interface GetConfirmSwapPreview {
    suspend operator fun invoke(swapQuote: SwapQuote): ConfirmSwapPreview
}

interface UpdateSlippageTolerance {
    suspend operator fun invoke(
        swapQuote: SwapQuote,
        slippageTolerance: Float,
        preview: ConfirmSwapPreview
    ): ConfirmSwapPreview
}

interface GetSwapTransactionStatusNavArgs {
    operator fun invoke(
        swapQuote: SwapQuote,
        signedTransactions: List<SwapQuoteTransaction>
    ): SwapTransactionStatusNavArgs
}