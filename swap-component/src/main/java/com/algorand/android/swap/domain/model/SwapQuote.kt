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

package com.algorand.android.swap.domain.model

import android.os.Parcelable
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.formatting.formatAmount
import java.math.BigDecimal
import kotlinx.parcelize.Parcelize

@Parcelize
data class SwapQuote(
    val quoteId: Long,
    val provider: SwapQuoteProvider?,
    val swapType: SwapType,
    val accountAddress: String,
    val fromAssetDetail: SwapQuoteAssetDetail,
    val toAssetDetail: SwapQuoteAssetDetail,
    val fromAssetAmount: BigDecimal,
    val fromAssetAmountInUsdValue: BigDecimal,
    val fromAssetAmountInSelectedCurrency: BigDecimal,
    val fromAssetAmountSelectedCurrencySymbol: String,
    val fromAssetAmountInSelectedCurrentFormattedValue: String,
    val fromAssetAmountWithSlippage: BigDecimal,
    val toAssetAmount: BigDecimal,
    val toAssetAmountInUsdValue: BigDecimal,
    val toAssetAmountInSelectedCurrency: BigDecimal,
    val toAssetAmountInSelectedCurrencyFormattedValue: String,
    val toAssetAmountSelectedCurrencySymbol: String,
    val toAssetAmountWithSlippage: BigDecimal,
    val price: Float,
    val priceImpact: Float,
    val peraFeeAmount: BigDecimal,
    val exchangeFeeAmount: BigDecimal,
    val slippage: Float
) : Parcelable {

    val isFromAssetAlgo: Boolean
        get() = fromAssetDetail.assetId == ALGO_ASSET_ID

    val isToAssetAlgo: Boolean
        get() = toAssetDetail.assetId == ALGO_ASSET_ID

    val totalFee: BigDecimal
        get() = peraFeeAmount.add(exchangeFeeAmount)

    fun getFormattedMinimumReceivedAmount(): String {
        return toAssetAmountWithSlippage
            .movePointLeft(toAssetDetail.fractionDecimals)
            .formatAmount(toAssetDetail.fractionDecimals, isDecimalFixed = false)
    }
}
