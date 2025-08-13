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

import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapper
import com.algorand.wallet.asset.domain.util.getSafeAssetIdForResponse
import com.algorand.wallet.swap.data.model.SwapQuoteAssetDetailResponse
import com.algorand.wallet.swap.domain.model.SwapQuote
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultSwapAssetDetailMapper @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper
) : SwapAssetDetailMapper {

    override fun invoke(response: SwapQuoteAssetDetailResponse?): SwapQuote.AssetDetail? {
        if (response == null) return null
        return SwapQuote.AssetDetail(
            assetId = getSafeAssetIdForResponse(response.assetId) ?: return null,
            logoUrl = response.logoUrl,
            name = response.name,
            shortName = response.shortName,
            total = response.total,
            fractionDecimals = response.fractionDecimals ?: return null,
            verificationTier = verificationTierMapper(response.verificationTierResponse),
            usdValue = response.usdValue ?: BigDecimal.ZERO,
        )
    }
}
