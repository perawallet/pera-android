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

package com.algorand.common.asset.data.mapper.model.collectible

import com.algorand.common.asset.data.database.model.AssetDetailEntity
import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.database.model.CollectibleTraitEntity
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.asset.domain.model.AudioCollectibleDetail
import com.algorand.common.asset.domain.model.ImageCollectibleDetail
import com.algorand.common.asset.domain.model.MixedCollectibleDetail
import com.algorand.common.asset.domain.model.UnsupportedCollectibleDetail
import com.algorand.common.asset.domain.model.VideoCollectibleDetail

internal interface ImageCollectibleDetailMapper {
    operator fun invoke(assetResponse: AssetResponse, collectibleResponse: CollectibleResponse): ImageCollectibleDetail?
    operator fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): ImageCollectibleDetail
}

internal interface MixedCollectibleDetailMapper {
    operator fun invoke(assetResponse: AssetResponse, collectibleResponse: CollectibleResponse): MixedCollectibleDetail?
    operator fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): MixedCollectibleDetail
}

internal interface UnsupportedCollectibleDetailMapper {
    operator fun invoke(
        assetResponse: AssetResponse,
        collectibleResponse: CollectibleResponse
    ): UnsupportedCollectibleDetail

    operator fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): UnsupportedCollectibleDetail
}

internal interface VideoCollectibleDetailMapper {
    operator fun invoke(assetResponse: AssetResponse, collectibleResponse: CollectibleResponse): VideoCollectibleDetail?
    operator fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): VideoCollectibleDetail
}

internal interface AudioCollectibleDetailMapper {
    operator fun invoke(assetResponse: AssetResponse, collectibleResponse: CollectibleResponse): AudioCollectibleDetail?
    operator fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        collectibleTraitEntities: List<CollectibleTraitEntity>?
    ): AudioCollectibleDetail
}
