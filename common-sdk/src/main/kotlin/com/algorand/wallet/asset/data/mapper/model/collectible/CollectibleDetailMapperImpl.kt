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

import com.algorand.wallet.asset.data.database.model.AssetDetailEntity
import com.algorand.wallet.asset.data.database.model.CollectibleEntity
import com.algorand.wallet.asset.data.database.model.CollectibleMediaEntity
import com.algorand.wallet.asset.data.database.model.CollectibleMediaTypeEntity
import com.algorand.wallet.asset.data.database.model.CollectibleTraitEntity
import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.collectible.CollectibleMediaTypeResponse.AUDIO
import com.algorand.wallet.asset.data.model.collectible.CollectibleMediaTypeResponse.IMAGE
import com.algorand.wallet.asset.data.model.collectible.CollectibleMediaTypeResponse.MIXED
import com.algorand.wallet.asset.data.model.collectible.CollectibleMediaTypeResponse.UNKNOWN
import com.algorand.wallet.asset.data.model.collectible.CollectibleMediaTypeResponse.VIDEO
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import javax.inject.Inject

internal class CollectibleDetailMapperImpl @Inject constructor(
    private val imageCollectibleDetailMapper: ImageCollectibleDetailMapper,
    private val mixedCollectibleDetailMapper: MixedCollectibleDetailMapper,
    private val videoCollectibleDetailMapper: VideoCollectibleDetailMapper,
    private val audioCollectibleDetailMapper: AudioCollectibleDetailMapper,
    private val unsupportedCollectibleDetailMapper: UnsupportedCollectibleDetailMapper
) : CollectibleDetailMapper {

    override fun invoke(response: AssetResponse): CollectibleDetail? {
        val collectible = response.collectible ?: return null
        return when (response.collectible.mediaType) {
            IMAGE -> imageCollectibleDetailMapper(response, collectible)
            VIDEO -> videoCollectibleDetailMapper(response, collectible)
            MIXED -> mixedCollectibleDetailMapper(response, collectible)
            UNKNOWN -> unsupportedCollectibleDetailMapper(response, collectible)
            AUDIO -> audioCollectibleDetailMapper(response, collectible)
            else -> null
        }
    }

    override fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        mediaEntities: List<CollectibleMediaEntity>?,
        traitEntities: List<CollectibleTraitEntity>?
    ): CollectibleDetail? {
        return when (collectibleEntity.mediaType) {
            CollectibleMediaTypeEntity.IMAGE -> {
                imageCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.VIDEO -> {
                videoCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.MIXED -> {
                mixedCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.AUDIO -> {
                audioCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.UNKNOWN -> {
                unsupportedCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            null -> null
        }
    }
}
