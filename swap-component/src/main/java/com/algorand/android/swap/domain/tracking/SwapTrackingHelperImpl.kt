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

package com.algorand.android.swap.domain.tracking

import com.algorand.android.assetutils.AssetConstants
import com.algorand.android.date.component.getUTCZonedDateTime
import com.algorand.android.formatting.TWO_DECIMALS
import com.algorand.android.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.swap.domain.model.SwapQuote
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

internal class SwapTrackingHelperImpl @Inject constructor(
    private val getUsdToAlgoConversionRate: GetUsdToAlgoConversionRate
) : SwapTrackingHelper {

    override fun getInputAsaAmountAsAlgo(swapQuote: SwapQuote): Double {
        return getAssetAmountAsAlgo(
            swappedAssetUsdValue = swapQuote.fromAssetAmountInUsdValue,
            swappedAssetAmount = swapQuote.fromAssetAmount,
            assetDecimal = swapQuote.fromAssetDetail.fractionDecimals,
            isAlgo = swapQuote.isFromAssetAlgo
        )
    }

    override fun getInputAsaAmountAsUsd(swapQuote: SwapQuote): Double {
        return swapQuote.fromAssetAmountInUsdValue
            .setScale(TWO_DECIMALS, CALCULATION_ROUNDING_MODE)
            .stripTrailingZeros()
            .toDouble()
    }

    override fun getOutputAsaAmountAsAlgo(swapQuote: SwapQuote): Double {
        return getAssetAmountAsAlgo(
            swappedAssetUsdValue = swapQuote.toAssetAmountInUsdValue,
            swappedAssetAmount = swapQuote.toAssetAmount,
            assetDecimal = swapQuote.toAssetDetail.fractionDecimals,
            isAlgo = swapQuote.isToAssetAlgo
        )
    }

    override fun getOutputAsaAmountAsUsd(swapQuote: SwapQuote): Double {
        return swapQuote.toAssetAmountInUsdValue
            .setScale(TWO_DECIMALS, CALCULATION_ROUNDING_MODE)
            .stripTrailingZeros()
            .toDouble()
    }

    override fun getSwapDateTimestamp(): Long {
        return getCurrentTimeUTC().toInstant().toEpochMilli()
    }

    override fun getFormattedSwapDateTime(): String {
        return getCurrentTimeUTC().format(DateTimeFormatter.ofPattern(DATE_AND_TIME_PATTERN, Locale.ENGLISH))
    }

    private fun getCurrentTimeUTC() = getUTCZonedDateTime()

    private fun getAssetAmountAsAlgo(
        swappedAssetUsdValue: BigDecimal,
        swappedAssetAmount: BigDecimal,
        assetDecimal: Int,
        isAlgo: Boolean
    ): Double {
        return if (isAlgo) {
            swappedAssetAmount.movePointLeft(AssetConstants.ALGO_DECIMALS)
        } else {
            val usdToAlgoRatio = getUsdToAlgoConversionRate()
            val assetUnitUsdValue = swappedAssetAmount
                .movePointLeft(assetDecimal)
                .multiply(swappedAssetUsdValue)
                .setScale(TWO_DECIMALS, CALCULATION_ROUNDING_MODE)
            assetUnitUsdValue.multiply(usdToAlgoRatio).setScale(assetDecimal, CALCULATION_ROUNDING_MODE)
        }.stripTrailingZeros().toDouble()
    }

    private companion object {
        val CALCULATION_ROUNDING_MODE = RoundingMode.DOWN
        const val DATE_AND_TIME_PATTERN = "MMMM dd, yyyy - HH:mm"
    }
}
