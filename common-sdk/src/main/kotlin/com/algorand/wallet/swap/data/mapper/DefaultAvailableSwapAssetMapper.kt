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
import com.algorand.wallet.swap.data.model.AvailableSwapAssetResponse
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import javax.inject.Inject

internal class DefaultAvailableSwapAssetMapper @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper
) : AvailableSwapAssetMapper {

    override fun invoke(response: AvailableSwapAssetResponse): AvailableSwapAsset? {
        return AvailableSwapAsset(
            assetId = getSafeAssetIdForResponse(response.assetId) ?: return null,
            name = response.assetName,
            unitName = response.assetName,
            logoUrl = response.logoUrl,
            verificationTier = verificationTierMapper(response.verificationTierResponse),
            usdValue = response.usdValue?.toBigDecimalOrNull(),
            addressBalance = null,
            decimals = response.fractionDecimals ?: return null
        )
    }
}
