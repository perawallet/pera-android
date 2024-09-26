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
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.VerificationTierMapper
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier.UNKNOWN
import com.algorand.android.assetutils.getSafeAssetIdForResponse
import com.algorand.android.module.swap.component.data.model.SwapQuoteAssetDetailResponse
import com.algorand.android.module.swap.component.domain.model.SwapQuoteAssetDetail
import java.math.BigInteger
import javax.inject.Inject

internal class SwapQuoteAssetDetailMapperImpl @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper
) : SwapQuoteAssetDetailMapper {

    override fun invoke(response: SwapQuoteAssetDetailResponse?): SwapQuoteAssetDetail? {
        return SwapQuoteAssetDetail(
            assetId = getSafeAssetIdForResponse(response?.assetId ?: return null) ?: return null,
            logoUrl = response.logoUrl.orEmpty(),
            name = response.name.orEmpty(),
            shortName = response.shortName.orEmpty(),
            total = response.total ?: BigInteger.ZERO,
            fractionDecimals = response.fractionDecimals ?: DEFAULT_ASSET_DECIMAL,
            verificationTier = response.verificationTierResponse?.let { verificationTierMapper(it) } ?: UNKNOWN,
            usdValue = response.usdValue
        )
    }
}
