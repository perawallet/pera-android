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

package com.algorand.android.assetdetail.component.collectible.data.mapper

import com.algorand.android.assetdetail.component.asset.data.mapper.model.VerificationTierMapper
import com.algorand.android.assetdetail.component.asset.data.model.AssetResponse
import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.AUDIO
import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.IMAGE
import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.VIDEO
import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleResponse
import com.algorand.android.assetdetail.component.asset.domain.model.detail.MixedCollectibleDetail
import com.algorand.android.assetdetail.component.collectible.domain.model.BaseCollectibleMedia
import com.algorand.android.assetdetail.component.mapper.AssetInfoMapper
import com.algorand.android.assetdetail.component.mapper.CollectibleInfoMapper
import com.algorand.android.shared_db.assetdetail.model.AssetDetailEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity
import javax.inject.Inject

internal class MixedCollectibleDetailMapperImpl @Inject constructor(
    private val assetInfoMapper: AssetInfoMapper,
    private val collectibleInfoMapper: CollectibleInfoMapper,
    private val verificationTierMapper: VerificationTierMapper
) : MixedCollectibleDetailMapper {

    override fun invoke(
        assetResponse: AssetResponse,
        collectibleResponse: CollectibleResponse
    ): MixedCollectibleDetail? {
        return MixedCollectibleDetail(
            id = assetResponse.assetId ?: return null,
            collectibleInfo = collectibleInfoMapper(collectibleResponse, assetResponse.explorerUrl),
            assetInfo = assetInfoMapper(assetResponse),
            verificationTier = verificationTierMapper(assetResponse.verificationTier),
            collectibleMedias = collectibleResponse.collectibleMedias?.map {
                it.mapToCollectibleMedia()
            }.orEmpty()
        )
    }

    override fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): MixedCollectibleDetail {
        return MixedCollectibleDetail(
            id = entity.assetId,
            collectibleInfo = collectibleInfoMapper(collectibleEntity, collectibleTraitEntities, entity.explorerUrl),
            assetInfo = assetInfoMapper(entity),
            verificationTier = verificationTierMapper(entity.verificationTier),
            collectibleMedias = collectibleMediaEntities?.map {
                it.mapToCollectibleMedia()
            }.orEmpty()
        )
    }

    private fun CollectibleMediaResponse.mapToCollectibleMedia(): BaseCollectibleMedia {
        return when (mediaType) {
            IMAGE -> BaseCollectibleMedia.ImageCollectibleMedia(downloadUrl, previewUrl)
            VIDEO -> BaseCollectibleMedia.VideoCollectibleMedia(downloadUrl, previewUrl)
            AUDIO -> BaseCollectibleMedia.AudioCollectibleMedia(downloadUrl, previewUrl)
            else -> BaseCollectibleMedia.UnsupportedCollectibleMedia(downloadUrl, previewUrl)
        }
    }

    private fun CollectibleMediaEntity.mapToCollectibleMedia(): BaseCollectibleMedia {
        return when (mediaType) {
            CollectibleMediaTypeEntity.IMAGE -> BaseCollectibleMedia.ImageCollectibleMedia(downloadUrl, previewUrl)
            CollectibleMediaTypeEntity.VIDEO -> BaseCollectibleMedia.VideoCollectibleMedia(downloadUrl, previewUrl)
            CollectibleMediaTypeEntity.AUDIO -> BaseCollectibleMedia.AudioCollectibleMedia(downloadUrl, previewUrl)
            else -> BaseCollectibleMedia.UnsupportedCollectibleMedia(downloadUrl, previewUrl)
        }
    }
}