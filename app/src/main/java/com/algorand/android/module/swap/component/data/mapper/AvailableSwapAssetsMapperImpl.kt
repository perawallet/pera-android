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

import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.VerificationTierMapper
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier.UNKNOWN
import com.algorand.android.assetutils.getSafeAssetIdForResponse
import com.algorand.android.module.swap.component.data.model.AvailableSwapAssetListResponse
import com.algorand.android.module.swap.component.domain.model.AvailableSwapAsset
import java.math.BigDecimal
import javax.inject.Inject

internal class AvailableSwapAssetsMapperImpl @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper
) : AvailableSwapAssetsMapper {

    override fun invoke(response: AvailableSwapAssetListResponse): List<AvailableSwapAsset> {
        return response.results?.mapNotNull {
            AvailableSwapAsset(
                assetId = getSafeAssetIdForResponse(it.assetId) ?: return@mapNotNull null,
                assetName = it.assetName,
                logoUrl = it.logoUrl,
                assetShortName = it.assetShortName,
                verificationTier = it.verificationTierResponse?.let { verificationTierMapper(it) } ?: UNKNOWN,
                usdValue = it.usdValue?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            )
        }.orEmpty()
    }
}
