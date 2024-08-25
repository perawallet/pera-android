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

package com.algorand.android.swap.domain.tracking.usecase

import com.algorand.android.eventtracker.NodeAwarePeraEventTracker
import com.algorand.android.formatting.TWO_DECIMALS
import com.algorand.android.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.tracking.SwapTrackingHelper
import com.algorand.android.swap.domain.tracking.mapper.SwapSuccessEventPayloadMapper
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

internal class LogSwapTransactionSuccessEventUseCase @Inject constructor(
    private val swapTrackingHelper: SwapTrackingHelper,
    private val swapSuccessEventPayloadMapper: SwapSuccessEventPayloadMapper,
    private val getUsdToAlgoConversionRate: GetUsdToAlgoConversionRate,
    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate,
    private val nodeAwarePeraEventTracker: NodeAwarePeraEventTracker
) : LogSwapTransactionSuccessEvent {

    override fun invoke(swapQuote: SwapQuote, networkFeeAsAlgo: Double) {
        val payload = getTrackingPayload(swapQuote, networkFeeAsAlgo)
        nodeAwarePeraEventTracker.logEvent(SWAP_SUCCESS_EVENT_KEY, payload)
    }

    private fun getTrackingPayload(swapQuote: SwapQuote, networkFeeAsAlgo: Double): Map<String, Any> {
        return with(swapQuote) {
            swapSuccessEventPayloadMapper(
                swapQuote = this,
                inputAsaAmountAsAlgo = swapTrackingHelper.getInputAsaAmountAsAlgo(swapQuote),
                inputAsaAmountAsUsd = swapTrackingHelper.getInputAsaAmountAsUsd(this),
                inputAsaAmount = getInputAsaAmount(this),
                outputAsaAmountAsAlgo = swapTrackingHelper.getOutputAsaAmountAsAlgo(swapQuote),
                outputAsaAmountAsUsd = swapTrackingHelper.getOutputAsaAmountAsUsd(this),
                outputAsaAmount = getOutputAsaAmount(this),
                swapDateTimestamp = swapTrackingHelper.getSwapDateTimestamp(),
                formattedSwapDateTime = swapTrackingHelper.getFormattedSwapDateTime(),
                peraFeeAsUsd = getPeraFeeAsUsd(peraFeeAmount),
                peraFeeAsAlgo = getPeraFeeAsAlgo(peraFeeAmount),
                exchangeFeeAsAlgo = getExchangeFeeAsAlgo(swapQuote),
                networkFeeAsAlgo = networkFeeAsAlgo
            )
        }
    }

    private fun getInputAsaAmount(swapQuote: SwapQuote): Double {
        return swapQuote.fromAssetAmount
            .movePointLeft(swapQuote.fromAssetDetail.fractionDecimals)
            .stripTrailingZeros()
            .toDouble()
    }

    private fun getOutputAsaAmount(swapQuote: SwapQuote): Double {
        return swapQuote.toAssetAmount
            .movePointLeft(swapQuote.toAssetDetail.fractionDecimals)
            .stripTrailingZeros()
            .toDouble()
    }

    private fun getPeraFeeAsUsd(peraFeeAsAlgo: BigDecimal): Double {
        val algoToUsdConversionRatio = getAlgoToUsdConversionRate()
        return peraFeeAsAlgo
            .multiply(algoToUsdConversionRatio)
            .setScale(TWO_DECIMALS, CALCULATION_ROUNDING_MODE)
            .stripTrailingZeros()
            .toDouble()
    }

    private fun getPeraFeeAsAlgo(peraFeeAsAlgo: BigDecimal): Double {
        return peraFeeAsAlgo.stripTrailingZeros().toDouble()
    }

    private fun getExchangeFeeAsAlgo(swapQuote: SwapQuote): Double {
        return with(swapQuote) {
            if (isFromAssetAlgo) {
                exchangeFeeAmount
            } else {
                val usdToAlgoRatio = getUsdToAlgoConversionRate()
                val assetUnitUsdValue = fromAssetAmount
                    .movePointLeft(fromAssetDetail.fractionDecimals)
                    .multiply(fromAssetAmountInUsdValue)
                val exchangeFeeAsUsd = exchangeFeeAmount.multiply(assetUnitUsdValue)
                exchangeFeeAsUsd
                    .multiply(usdToAlgoRatio)
                    .setScale(fromAssetDetail.fractionDecimals, CALCULATION_ROUNDING_MODE)
            }
        }.stripTrailingZeros().toDouble()
    }

    private companion object {
        val CALCULATION_ROUNDING_MODE = RoundingMode.DOWN
        const val SWAP_SUCCESS_EVENT_KEY = "swapscr_assets_completed"
    }
}
