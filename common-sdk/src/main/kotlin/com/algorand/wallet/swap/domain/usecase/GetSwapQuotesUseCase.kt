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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.model.SwapQuote
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuotes
import com.algorand.wallet.swap.domain.repository.SwapRepository
import javax.inject.Inject

internal class GetSwapQuotesUseCase @Inject constructor(
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val swapRepository: SwapRepository,
    private val getSwapQuoteDetails: GetSwapQuoteDetails
) : GetSwapQuotes {

    override suspend fun invoke(payload: SwapQuotePayload): PeraResult<SwapQuotes> {
        return swapRepository.getSwapQuotes(getRequestPayload(payload)).use(
            onSuccess = { quotes -> getSuccessResult(quotes) },
            onFailed = { exception, code -> PeraResult.Error(exception, code) }
        )
    }

    private suspend fun getSuccessResult(quotes: List<SwapQuote>): PeraResult<SwapQuotes> {
        return if (quotes.isEmpty()) {
            PeraResult.Error(IllegalStateException())
        } else {
            val bestOfferQuoteId = quotes.maxBy { it.assetOutAmount.amount }.quoteId
            val quoteDetails = getSwapQuoteDetails(quotes)
            val swapQuotes = SwapQuotes(selectedQuoteId = bestOfferQuoteId, bestOfferQuoteId, quoteDetails)
            PeraResult.Success(swapQuotes)
        }
    }

    private fun getRequestPayload(payload: SwapQuotePayload): SwapQuoteRequestPayload {
        return SwapQuoteRequestPayload(
            address = payload.address,
            assetInId = payload.assetInId,
            assetOutId = payload.assetOutId,
            amount = payload.amount,
            deviceId = getSelectedNodeDeviceId().orEmpty(),
            providers = SwapQuoteProvider.entries,
            slippage = payload.slippage
        )
    }
}
