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

package com.algorand.android.modules.accountcore.domain.mapper

import com.algorand.android.assetsearch.domain.mapper.LegacyVerificationTierMapper
import com.algorand.android.models.BaseAccountAssetData
import com.algorand.wallet.asset.lite.domain.model.AssetDetailLite
import javax.inject.Inject

internal class PendingAdditionAssetDataMapperImpl @Inject constructor(
    private val legacyVerificationTierMapper: LegacyVerificationTierMapper
) : PendingAdditionAssetDataMapper {

    override fun invoke(assetDetailLite: AssetDetailLite): BaseAccountAssetData.PendingAssetData.AdditionAssetData {
        return BaseAccountAssetData.PendingAssetData.AdditionAssetData(
            id = assetDetailLite.id,
            name = assetDetailLite.name,
            shortName = assetDetailLite.shortName,
            isAlgo = false,
            decimals = assetDetailLite.decimals,
            creatorPublicKey = assetDetailLite.creatorAddress,
            usdValue = assetDetailLite.usdValue,
            verificationTier = legacyVerificationTierMapper(assetDetailLite.verificationTier)
        )
    }
}
