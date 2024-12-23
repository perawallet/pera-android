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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.database.model.CollectibleTraitEntity
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleTraitMapper
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.asset.domain.model.CollectibleDetail

internal class CollectibleInfoMapperImpl(
    private val collectibleTraitMapper: CollectibleTraitMapper
) : CollectibleInfoMapper {

    override fun invoke(response: CollectibleResponse, explorerUrl: String?): CollectibleDetail.CollectibleInfo {
        return CollectibleDetail.CollectibleInfo(
            title = response.title,
            collectionName = response.collection?.collectionName,
            collectibleDescription = response.description,
            traits = response.traits?.mapNotNull { collectibleTraitMapper(it) }.orEmpty(),
            nftExplorerUrl = explorerUrl,
            prismUrl = response.primaryImageUrl,
            primaryImageUrl = response.getPrimaryImageUrl()
        )
    }

    override fun invoke(
        entity: CollectibleEntity,
        traitEntities: List<CollectibleTraitEntity>?,
        explorerUrl: String?
    ): CollectibleDetail.CollectibleInfo {
        return CollectibleDetail.CollectibleInfo(
            title = entity.title,
            collectionName = entity.collectionName,
            collectibleDescription = entity.description,
            traits = collectibleTraitMapper(traitEntities.orEmpty()),
            nftExplorerUrl = explorerUrl,
            prismUrl = entity.primaryImageUrl,
            primaryImageUrl = entity.primaryImageUrl
        )
    }

    private fun CollectibleResponse.getPrimaryImageUrl(): String? {
        return primaryImageUrl ?: collectibleMedias?.firstOrNull()?.run { previewUrl ?: downloadUrl }
    }
}
