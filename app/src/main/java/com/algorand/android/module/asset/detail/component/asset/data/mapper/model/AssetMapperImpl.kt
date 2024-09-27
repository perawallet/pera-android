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

package com.algorand.android.module.asset.detail.component.asset.data.mapper.model

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.NodeAssetDetailResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.CollectibleDetailMapper
import com.algorand.android.module.shareddb.assetdetail.model.AssetDetailEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity
import javax.inject.Inject

internal class AssetMapperImpl @Inject constructor(
    private val collectibleDetailMapper: CollectibleDetailMapper,
    private val assetDetailMapper: AssetDetailMapper
) : AssetMapper {

    override fun invoke(response: AssetResponse): Asset? {
        return if (response.collectible != null) {
            collectibleDetailMapper(response)
        } else {
            assetDetailMapper(response)
        }
    }

    override fun invoke(assetId: Long, nodeResponse: NodeAssetDetailResponse): AssetDetail {
        return assetDetailMapper(assetId, nodeResponse)
    }

    override fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity?,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): Asset {
        return if (collectibleEntity != null) {
            collectibleDetailMapper(
                entity,
                collectibleEntity,
                collectibleMediaEntities,
                collectibleTraitEntities
            ) ?: assetDetailMapper(entity)
        } else {
            assetDetailMapper(entity)
        }
    }
}

/*
AssetDetail(
            assetId = response.assetId ?: return null,
            fullName = response.fullName,
            shortName = response.shortName,
            fractionDecimals = response.fractionDecimals ?: 0,
            usdValue = response.usdValue,
            assetCreator = response.assetCreator?.let { assetCreatorMapper(it) },
            verificationTier = response.verificationTier?.let { verificationTierMapper(it) } ?: UNKNOWN,
            logoUri = response.logoUri,
            logoSvgUri = response.logoSvgUri,
            explorerUrl = response.explorerUrl,
            projectUrl = response.projectUrl,
            projectName = response.projectName,
            discordUrl = response.discordUrl,
            telegramUrl = response.telegramUrl,
            twitterUsername = response.twitterUsername,
            assetDescription = response.description,
            totalSupply = response.totalSupply,
            maxSupply = response.maxSupply,
            url = response.url,
            last24HoursAlgoPriceChangePercentage = response.last24HoursAlgoPriceChangePercentage,
            isAvailableOnDiscoverMobile = response.isAvailableOnDiscoverMobile,
            collectible = response.collectible?.let { collectibleMapper(it) }
        )
 */