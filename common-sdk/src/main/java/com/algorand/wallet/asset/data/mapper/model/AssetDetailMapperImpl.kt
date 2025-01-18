/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.NodeAssetDetailResponse
import com.algorand.wallet.asset.domain.model.AssetDetail
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.data.database.model.AssetDetailEntity
import javax.inject.Inject

internal class AssetDetailMapperImpl @Inject constructor(
    private val assetInfoMapper: AssetInfoMapper,
    private val verificationTierMapper: VerificationTierMapper
) : AssetDetailMapper {

    override fun invoke(response: AssetResponse): AssetDetail? {
        return AssetDetail(
            id = response.assetId ?: return null,
            assetInfo = assetInfoMapper(response),
            verificationTier = verificationTierMapper(response.verificationTier)
        )
    }

    override fun invoke(assetId: Long, nodeResponse: NodeAssetDetailResponse): AssetDetail {
        return AssetDetail(
            id = assetId,
            verificationTier = VerificationTier.UNKNOWN,
            assetInfo = assetInfoMapper(nodeResponse)
        )
    }

    override fun invoke(entity: AssetDetailEntity): AssetDetail {
        return AssetDetail(
            id = entity.assetId,
            verificationTier = verificationTierMapper(entity.verificationTier),
            assetInfo = assetInfoMapper(entity)
        )
    }
}
