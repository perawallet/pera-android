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

import com.algorand.android.module.asset.detail.component.AssetConstants.DEFAULT_ASSET_DECIMAL
import com.algorand.android.module.asset.utils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.module.asset.utils.AssetConstants.ALGO_ID
import com.algorand.android.foundation.common.toBigDecimalOrZero
import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.GetUsdValuePerAsset
import com.algorand.android.module.swap.component.data.SwapUtils.DEFAULT_EXCHANGE_SWAP_FEE
import com.algorand.android.module.swap.component.data.SwapUtils.DEFAULT_PERA_SWAP_FEE
import com.algorand.android.module.swap.component.data.model.SwapQuoteResponse
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class SwapQuoteMapperImpl @Inject constructor(
    private val swapTypeMapper: SwapTypeMapper,
    private val swapQuoteProviderMapper: SwapQuoteProviderMapper,
    private val swapQuoteAssetDetailMapper: SwapQuoteAssetDetailMapper,
    private val displayedCurrencyParityValueMapper: DisplayedCurrencyParityValueMapper,
    private val getUsdValuePerAsset: GetUsdValuePerAsset
) : SwapQuoteMapper {

    override fun invoke(response: SwapQuoteResponse?): SwapQuote? {
        if (response == null) return null
        val fromAssetParity = getFromAssetAmountInSelectedCurrency(response)
        val toAssetParity = getToAssetAmountInSelectedCurrency(response)
        return SwapQuote(
            quoteId = response.id ?: return null,
            swapType = swapTypeMapper(response.swapType),
            provider = swapQuoteProviderMapper(response.provider),
            accountAddress = response.swapperAddress ?: return null,
            fromAssetDetail = swapQuoteAssetDetailMapper(response.assetInAssetDetailResponse) ?: return null,
            toAssetDetail = swapQuoteAssetDetailMapper(response.assetOutAssetDetailResponse) ?: return null,
            fromAssetAmount = response.assetInAmount.toBigDecimalOrZero(),
            fromAssetAmountInUsdValue = response.assetInAmountInUsdValue.toBigDecimalOrZero(),
            fromAssetAmountInSelectedCurrency = fromAssetParity.amountAsCurrency,
            fromAssetAmountSelectedCurrencySymbol = fromAssetParity.selectedCurrencySymbol,
            fromAssetAmountInSelectedCurrentFormattedValue = fromAssetParity.getFormattedValue(),
            fromAssetAmountWithSlippage = response.assetInAmountWithSlippage.toBigDecimalOrZero(),
            toAssetAmount = response.assetOutAmount.toBigDecimalOrZero(),
            toAssetAmountInUsdValue = response.assetOutAmountInUsdValue.toBigDecimalOrZero(),
            toAssetAmountInSelectedCurrency = toAssetParity.amountAsCurrency,
            toAssetAmountSelectedCurrencySymbol = toAssetParity.selectedCurrencySymbol,
            toAssetAmountInSelectedCurrencyFormattedValue = toAssetParity.getFormattedValue(),
            toAssetAmountWithSlippage = response.assetOutAmountWithSlippage.toBigDecimalOrZero(),
            price = response.price?.toFloatOrNull() ?: 0f,
            priceImpact = getPriceImpact(response.priceImpact),
            peraFeeAmount = response.peraFeeAmount?.movePointLeft(ALGO_DECIMALS) ?: DEFAULT_PERA_SWAP_FEE,
            exchangeFeeAmount = getExchangeFeeAmount(
                response.exchangeFeeAmount,
                response.assetInAssetDetailResponse?.fractionDecimals
            ),
            slippage = getSlippage(response.slippage),
        )
    }

    private fun getSlippage(slippage: String?): Float {
        val slippageAsFloat = slippage?.toFloatOrNull() ?: 0f
        return slippageAsFloat * SLIPPAGE_TOLERANCE_RESPONSE_MULTIPLIER
    }

    private fun getPriceImpact(priceImpact: String?): Float {
        val priceImpactAsFloat = priceImpact?.toFloatOrNull() ?: 0f
        return priceImpactAsFloat * PRICE_IMPACT_RESPONSE_MULTIPLIER
    }

    private fun getExchangeFeeAmount(amount: BigDecimal?, fromAssetDecimals: Int?): BigDecimal {
        val assetDecimals = fromAssetDecimals ?: DEFAULT_ASSET_DECIMAL_FOR_SWAP
        return amount?.movePointLeft(assetDecimals) ?: DEFAULT_EXCHANGE_SWAP_FEE
    }

    private fun getFromAssetAmountInSelectedCurrency(response: SwapQuoteResponse): ParityValue {
        return with(response) {
            val usdValuePerAsset = getUsdValuePerAsset(
                assetAmount = assetInAmount,
                assetDecimal = assetInAssetDetailResponse?.fractionDecimals,
                totalAssetAmountInUsdValue = assetInAmountInUsdValue
            )
            displayedCurrencyParityValueMapper(
                assetAmount = assetInAmount?.toBigIntegerOrNull() ?: BigInteger.ZERO,
                assetUsdValue = usdValuePerAsset,
                assetDecimal = assetInAssetDetailResponse?.fractionDecimals ?: DEFAULT_ASSET_DECIMAL_FOR_SWAP,
                assetId = assetInAssetDetailResponse?.assetId ?: 0
            )
        }
    }

    private fun getToAssetAmountInSelectedCurrency(response: SwapQuoteResponse): ParityValue {
        return with(response) {
            val usdValuePerAsset = getUsdValuePerAsset(
                assetAmount = assetOutAmount,
                assetDecimal = assetOutAssetDetailResponse?.fractionDecimals,
                totalAssetAmountInUsdValue = assetOutAmountInUsdValue
            )
            displayedCurrencyParityValueMapper(
                assetAmount = assetOutAmount?.toBigIntegerOrNull() ?: BigInteger.ZERO,
                assetUsdValue = usdValuePerAsset,
                assetDecimal = assetOutAssetDetailResponse?.fractionDecimals ?: DEFAULT_ASSET_DECIMAL_FOR_SWAP,
                assetId = assetOutAssetDetailResponse?.assetId ?: FALLBACK_ASSET_ID_FOR_QUOTE
            )
        }
    }

    companion object {
        private const val PRICE_IMPACT_RESPONSE_MULTIPLIER = 100f
        private const val SLIPPAGE_TOLERANCE_RESPONSE_MULTIPLIER = 100f
        private const val DEFAULT_ASSET_DECIMAL_FOR_SWAP = DEFAULT_ASSET_DECIMAL
        private const val FALLBACK_ASSET_ID_FOR_QUOTE = ALGO_ID
    }
}
