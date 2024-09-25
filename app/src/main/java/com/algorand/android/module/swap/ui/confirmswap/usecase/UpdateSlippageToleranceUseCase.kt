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

package com.algorand.android.module.swap.ui.confirmswap.usecase

import com.algorand.android.foundation.Event
import com.algorand.android.swap.domain.model.GetSwapQuotePayload
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.model.SwapType
import com.algorand.android.swap.domain.usecase.GetSwapQuote
import com.algorand.android.module.swap.ui.GetSwapError
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPreview
import java.math.BigDecimal
import javax.inject.Inject

internal class UpdateSlippageToleranceUseCase @Inject constructor(
    private val getSwapQuote: GetSwapQuote,
    private val getConfirmSwapPreview: GetConfirmSwapPreview,
    private val getSwapError: GetSwapError
) : UpdateSlippageTolerance {

    override suspend fun invoke(
        swapQuote: SwapQuote,
        slippageTolerance: Float,
        preview: ConfirmSwapPreview
    ): ConfirmSwapPreview {
        return with(swapQuote) {
            if (slippage == slippageTolerance) return preview
            val swapAmount = if (swapType == SwapType.FIXED_INPUT) fromAssetAmount else toAssetAmount
            getSwapQuote(getSwapQuotePayload(swapQuote, swapAmount, slippageTolerance)).use(
                onSuccess = { newSwapQuote ->
                    val newState = getConfirmSwapPreview(newSwapQuote).copy(
                        slippageToleranceUpdateSuccessEvent = Event(Unit)
                    )
                    newState
                },
                onFailed = { exception, code ->
                    val errorMessage = exception.message
                    val errorEvent = if (!errorMessage.isNullOrBlank()) {
                        Event(getSwapError(errorMessage))
                    } else {
                        null
                    }
                    val newState = preview.copy(
                        isLoading = false,
                        errorEvent = errorEvent
                    )
                    newState
                }
            )
        }
    }

    private fun getSwapQuotePayload(
        swapQuote: SwapQuote,
        swapAmount: BigDecimal,
        slippageTolerance: Float
    ): GetSwapQuotePayload {
        return with(swapQuote) {
            GetSwapQuotePayload(
                fromAssetId = fromAssetDetail.assetId,
                toAssetId = toAssetDetail.assetId,
                amount = swapAmount.toBigInteger(),
                accountAddress = accountAddress,
                slippage = slippageTolerance
            )
        }
    }
}
