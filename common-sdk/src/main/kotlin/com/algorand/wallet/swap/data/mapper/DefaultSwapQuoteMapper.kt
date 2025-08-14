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

package com.algorand.wallet.swap.data.mapper

import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.swap.data.model.SwapQuoteResponse
import com.algorand.wallet.swap.domain.model.SwapQuote
import com.algorand.wallet.swap.domain.model.SwapType
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultSwapQuoteMapper @Inject constructor(
    private val quoteProviderMapper: SwapQuoteProviderMapper,
    private val assetDetailMapper: SwapAssetDetailMapper,
    private val assetAmountMapper: SwapAssetAmountMapper
) : SwapQuoteMapper {

    override fun invoke(response: SwapQuoteResponse): SwapQuote? {
        return SwapQuote(
            quoteId = response.id ?: return null,
            accountAddress = response.swapperAddress ?: return null,
            provider = quoteProviderMapper(response.provider) ?: return null,
            swapType = SwapType.FIXED_INPUT,
            assetInDetail = assetDetailMapper(response.assetInAssetDetailResponse) ?: return null,
            assetOutDetail = assetDetailMapper(response.assetOutAssetDetailResponse) ?: return null,
            assetInAmount = assetAmountMapper.mapAssetInAmount(response) ?: return null,
            assetOutAmount = assetAmountMapper.mapAssetOutAmount(response) ?: return null,
            price = response.price?.toFloatOrNull() ?: return null,
            priceImpact = response.priceImpact?.toFloatOrNull() ?: 0f,
            slippage = response.slippage?.toFloatOrNull() ?: 0f,
            fee = mapSwapFee(response)
        )
    }

    private fun mapSwapFee(response: SwapQuoteResponse): SwapQuote.SwapFee {
        val peraFee = response.peraFeeAmount?.toBigDecimal()?.movePointLeft(ALGO_DECIMALS) ?: BigDecimal.ZERO
        val exchangeFee = response.exchangeFeeAmount?.toBigDecimal()?.movePointLeft(ALGO_DECIMALS) ?: BigDecimal.ZERO
        val totalFee = peraFee + exchangeFee
        return SwapQuote.SwapFee(peraFee, exchangeFee, totalFee)
    }
}
