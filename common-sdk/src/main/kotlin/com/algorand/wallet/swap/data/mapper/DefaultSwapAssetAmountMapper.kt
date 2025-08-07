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

import com.algorand.wallet.swap.data.model.SwapQuoteResponse
import com.algorand.wallet.swap.domain.model.SwapQuote
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultSwapAssetAmountMapper @Inject constructor() : SwapAssetAmountMapper {

    override fun mapAssetInAmount(response: SwapQuoteResponse): SwapQuote.AssetAmount? {
        return with(response) {
            val decimal = assetInAssetDetailResponse?.fractionDecimals ?: return null
            if (assetInAmount == null || assetInAmountWithSlippage == null) return null
            val amount = assetInAmount.toBigDecimal().movePointLeft(decimal)
            val amountInUsd = assetInAmountInUsdValue ?: BigDecimal.ZERO
            val amountWithSlippage = assetInAmountWithSlippage.toBigDecimal().movePointLeft(decimal)
            SwapQuote.AssetAmount(amount, amountInUsd, amountWithSlippage)
        }
    }

    override fun mapAssetOutAmount(response: SwapQuoteResponse): SwapQuote.AssetAmount? {
        return with(response) {
            val decimal = assetOutAssetDetailResponse?.fractionDecimals ?: return null
            if (assetOutAmount == null || assetOutAmountWithSlippage == null) return null
            val amount = assetOutAmount.toBigDecimal().movePointLeft(decimal)
            val amountInUsd = assetOutAmountInUsdValue ?: BigDecimal.ZERO
            val amountWithSlippage = assetOutAmountWithSlippage.toBigDecimal().movePointLeft(decimal)
            SwapQuote.AssetAmount(amount, amountInUsd, amountWithSlippage)
        }
    }
}
