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

package com.algorand.android.ui.swap.confirmation.mapper

import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.modules.swap.assetswap.domain.model.SwapQuoteAssetDetail
import com.algorand.android.utils.AssetName
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapType
import java.math.BigDecimal
import javax.inject.Inject

typealias LegacySwapQuote = com.algorand.android.modules.swap.assetswap.domain.model.SwapQuote
typealias LegacySwapType = com.algorand.android.modules.swap.assetselection.base.ui.model.SwapType
typealias LegacyVerificationTier = com.algorand.android.assetsearch.domain.model.VerificationTier

internal class DefaultLegacySwapQuoteMapper @Inject constructor() : LegacySwapQuoteMapper {

    override fun invoke(swapQuoteV2: SwapQuoteV2): LegacySwapQuote {
        return with(swapQuoteV2) {
            val assetInDecimals = assetInDetail.fractionDecimals
            val assetOutDecimals = assetOutDetail.fractionDecimals
            LegacySwapQuote(
                quoteId = quoteId,
                provider = null, // Not being used in legacy code
                swapType = mapLegacySwapType(swapType),
                deviceId = null, // Not being used in legacy code
                accountAddress = accountAddress,
                fromAssetDetail = mapSwapQuoteAssetDetail(assetInDetail),
                toAssetDetail = mapSwapQuoteAssetDetail(assetOutDetail),
                fromAssetAmount = assetInAmount.amount.movePointRight(assetInDecimals),
                fromAssetAmountInUsdValue = assetInAmount.amountInUsdValue,
                fromAssetAmountInSelectedCurrency = ParityValue(BigDecimal.ZERO, ""), // Not being used in next flow
                fromAssetAmountWithSlippage = assetInAmount.amountWithSlippage.movePointRight(assetInDecimals),
                toAssetAmount = assetOutAmount.amount.movePointRight(assetOutDecimals),
                toAssetAmountInUsdValue = assetOutAmount.amountInUsdValue,
                toAssetAmountInSelectedCurrency = ParityValue(BigDecimal.ZERO, ""), // Not being used in next flow
                toAssetAmountWithSlippage = assetOutAmount.amountWithSlippage.movePointRight(assetOutDecimals),
                priceImpact = priceImpact,
                peraFeeAmount = fee.peraFeeAmount,
                exchangeFeeAmount = fee.exchangeFeeAmount,
                slippage = slippage,
                price = price
            )
        }
    }

    private fun mapSwapQuoteAssetDetail(assetDetail: SwapQuoteV2.AssetDetail): SwapQuoteAssetDetail {
        return SwapQuoteAssetDetail(
            assetId = assetDetail.assetId,
            logoUrl = assetDetail.logoUrl,
            name = AssetName.create(assetDetail.name),
            shortName = AssetName.createShortName(assetDetail.shortName),
            total = assetDetail.total,
            fractionDecimals = assetDetail.fractionDecimals,
            verificationTier = mapLegacyVerificationTier(assetDetail.verificationTier),
            usdValue = assetDetail.usdValue
        )
    }

    private fun mapLegacySwapType(swapType: SwapType): LegacySwapType {
        return when (swapType) {
            SwapType.FIXED_OUTPUT -> LegacySwapType.FIXED_OUTPUT
            SwapType.FIXED_INPUT -> LegacySwapType.FIXED_INPUT
        }
    }

    private fun mapLegacyVerificationTier(verificationTier: VerificationTier): LegacyVerificationTier {
        return when (verificationTier) {
            VerificationTier.VERIFIED -> LegacyVerificationTier.VERIFIED
            VerificationTier.UNVERIFIED -> LegacyVerificationTier.UNVERIFIED
            VerificationTier.TRUSTED -> LegacyVerificationTier.TRUSTED
            VerificationTier.SUSPICIOUS -> LegacyVerificationTier.SUSPICIOUS
            VerificationTier.UNKNOWN -> LegacyVerificationTier.SUSPICIOUS
        }
    }
}
