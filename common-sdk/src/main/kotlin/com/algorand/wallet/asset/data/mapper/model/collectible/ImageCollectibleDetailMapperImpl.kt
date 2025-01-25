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

package com.algorand.wallet.asset.data.mapper.model.collectible

import com.algorand.wallet.asset.data.mapper.model.AssetInfoMapper
import com.algorand.wallet.asset.data.mapper.model.CollectibleInfoMapper
import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapper
import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.collectible.CollectibleResponse
import com.algorand.wallet.asset.domain.model.BaseCollectibleMedia
import com.algorand.wallet.asset.domain.model.ImageCollectibleDetail
import com.algorand.wallet.asset.data.database.model.AssetDetailEntity
import com.algorand.wallet.asset.data.database.model.CollectibleEntity
import com.algorand.wallet.asset.data.database.model.CollectibleMediaEntity
import com.algorand.wallet.asset.data.database.model.CollectibleTraitEntity
import javax.inject.Inject

internal class ImageCollectibleDetailMapperImpl @Inject constructor(
    private val assetInfoMapper: AssetInfoMapper,
    private val collectibleInfoMapper: CollectibleInfoMapper,
    private val verificationTierMapper: VerificationTierMapper
) : ImageCollectibleDetailMapper {

    override fun invoke(
        assetResponse: AssetResponse,
        collectibleResponse: CollectibleResponse
    ): ImageCollectibleDetail? {
        return ImageCollectibleDetail(
            id = assetResponse.assetId ?: return null,
            collectibleInfo = collectibleInfoMapper(collectibleResponse, assetResponse.explorerUrl),
            assetInfo = assetInfoMapper(assetResponse),
            verificationTier = verificationTierMapper(assetResponse.verificationTier),
            collectibleMedias = collectibleResponse.collectibleMedias?.map {
                BaseCollectibleMedia.ImageCollectibleMedia(it.downloadUrl, it.previewUrl)
            }.orEmpty()
        )
    }

    override fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): ImageCollectibleDetail {
        return ImageCollectibleDetail(
            id = entity.assetId,
            collectibleInfo = collectibleInfoMapper(collectibleEntity, collectibleTraitEntities, entity.explorerUrl),
            assetInfo = assetInfoMapper(entity),
            verificationTier = verificationTierMapper(entity.verificationTier),
            collectibleMedias = collectibleMediaEntities?.map {
                BaseCollectibleMedia.ImageCollectibleMedia(it.downloadUrl, it.previewUrl)
            }.orEmpty()
        )
    }
}
