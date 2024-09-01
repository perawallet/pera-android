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
import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleResponse
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AudioCollectibleDetail
import com.algorand.android.assetdetail.component.collectible.domain.model.BaseCollectibleMedia
import com.algorand.android.assetdetail.component.mapper.AssetInfoMapper
import com.algorand.android.assetdetail.component.mapper.CollectibleInfoMapper
import com.algorand.android.shared_db.assetdetail.model.AssetDetailEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity
import javax.inject.Inject

internal class AudioCollectibleDetailMapperImpl @Inject constructor(
    private val assetInfoMapper: AssetInfoMapper,
    private val collectibleInfoMapper: CollectibleInfoMapper,
    private val verificationTierMapper: VerificationTierMapper
) : AudioCollectibleDetailMapper {

    override fun invoke(
        assetResponse: AssetResponse,
        collectibleResponse: CollectibleResponse
    ): AudioCollectibleDetail? {
        return AudioCollectibleDetail(
            id = assetResponse.assetId ?: return null,
            collectibleInfo = collectibleInfoMapper(collectibleResponse, assetResponse.explorerUrl),
            collectibleMedias = collectibleResponse.collectibleMedias?.map {
                BaseCollectibleMedia.AudioCollectibleMedia(it.downloadUrl, it.previewUrl)
            }.orEmpty(),
            assetInfo = assetInfoMapper(assetResponse),
            verificationTier = verificationTierMapper(assetResponse.verificationTier)
        )
    }

    override fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): AudioCollectibleDetail {
        return AudioCollectibleDetail(
            id = entity.assetId,
            collectibleInfo = collectibleInfoMapper(collectibleEntity, collectibleTraitEntities, entity.explorerUrl),
            collectibleMedias = collectibleMediaEntities?.map {
                BaseCollectibleMedia.AudioCollectibleMedia(it.downloadUrl, it.previewUrl)
            }.orEmpty(),
            assetInfo = assetInfoMapper(entity),
            verificationTier = verificationTierMapper(entity.verificationTier)
        )
    }
}