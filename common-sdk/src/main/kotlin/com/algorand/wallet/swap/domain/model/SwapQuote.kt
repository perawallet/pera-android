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

package com.algorand.wallet.swap.domain.model

import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigDecimal
import java.math.BigInteger

data class SwapQuote(
    val quoteId: Long,
    val provider: SwapQuoteProvider,
    val swapType: SwapType,
    val accountAddress: String,
    val assetInDetail: AssetDetail,
    val assetOutDetail: AssetDetail,
    val assetInAmount: AssetAmount,
    val assetOutAmount: AssetAmount,
    val price: Float,
    val priceImpact: Float,
    val fee: SwapFee,
    val slippage: Float
) {

    val isAssetInAlgo: Boolean
        get() = assetInDetail.assetId == ALGO_ID

    val isAssetOutAlgo: Boolean
        get() = assetOutDetail.assetId == ALGO_ID

    data class SwapFee(
        val peraFeeAmount: BigDecimal,
        val exchangeFeeAmount: BigDecimal,
        val totalFee: BigDecimal
    )

    data class AssetAmount(
        val amount: BigDecimal,
        val amountInUsdValue: BigDecimal,
        val amountWithSlippage: BigDecimal
    )

    data class AssetDetail(
        val assetId: Long,
        val logoUrl: String?,
        val name: String?,
        val shortName: String?,
        val total: BigInteger?,
        val fractionDecimals: Int,
        val verificationTier: VerificationTier,
        val usdValue: BigDecimal
    )
}
