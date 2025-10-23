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

import com.algorand.wallet.account.detail.domain.model.AccountType.LedgerBle
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle.LEDGER_DEFLEX_FILTER
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuotes
import com.algorand.wallet.swap.domain.repository.SwapRepository
import javax.inject.Inject

internal class GetSwapQuotesUseCase @Inject constructor(
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val swapRepository: SwapRepository,
    private val getSwapQuoteDetails: GetSwapQuoteDetails,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val getAccountType: GetAccountType
) : GetSwapQuotes {

    override suspend fun invoke(payload: SwapQuotePayload): PeraResult<SwapQuotes> {
        return swapRepository.getSwapQuotes(getRequestPayload(payload)).use(
            onSuccess = { quotes ->
                val filteredQuotes = getFilteredQuotes(payload.address, quotes)
                getSuccessResult(filteredQuotes)
            },
            onFailed = { exception, code -> PeraResult.Error(exception, code) }
        )
    }

    private suspend fun getSuccessResult(quotes: List<SwapQuoteV2>): PeraResult<SwapQuotes> {
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
            slippage = payload.slippage
        )
    }

    private suspend fun getFilteredQuotes(address: String, quotes: List<SwapQuoteV2>): List<SwapQuoteV2> {
        return if (isFeatureToggleEnabled(LEDGER_DEFLEX_FILTER.key) && getAccountType(address) is LedgerBle) {
            quotes.filter { it.provider.name != DEFLEX_PROVIDER_NAME }
        } else {
            quotes
        }
    }

    private companion object {
        private const val DEFLEX_PROVIDER_NAME = "deflex"
    }
}
