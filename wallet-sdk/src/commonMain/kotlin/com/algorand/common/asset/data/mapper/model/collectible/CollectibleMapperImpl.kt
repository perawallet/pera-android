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

import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.database.model.CollectibleTraitEntity
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.asset.domain.model.Collectible

internal class CollectibleMapperImpl(
    private val collectibleStandardTypeMapper: CollectibleStandardTypeMapper,
    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper,
    private val collectionMapper: CollectionMapper,
    private val collectibleMediaMapper: CollectibleMediaMapper,
    private val collectibleTraitMapper: CollectibleTraitMapper
) : CollectibleMapper {

    override fun invoke(response: CollectibleResponse): Collectible? {
        if (!response.isValid()) return null
        return Collectible(
            standardType = response.standard?.let { collectibleStandardTypeMapper(it) },
            mediaType = response.mediaType?.let { collectibleMediaTypeMapper(it) },
            primaryImageUrl = response.primaryImageUrl,
            title = response.title,
            collection = response.collection?.let { collectionMapper(it) },
            collectibleMedias = response.collectibleMedias?.mapNotNull { collectibleMediaMapper(it) }.orEmpty(),
            description = response.description,
            traits = response.traits?.mapNotNull { collectibleTraitMapper(it) }.orEmpty()
        )
    }

    override fun invoke(
        entity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        traitEntities: List<CollectibleTraitEntity>?
    ): Collectible {
        return with(entity) {
            Collectible(
                standardType = standardType?.let { collectibleStandardTypeMapper(it) },
                mediaType = mediaType?.let { collectibleMediaTypeMapper(it) },
                primaryImageUrl = primaryImageUrl,
                title = title,
                collection = collectionMapper(collectionId, collectionName, collectionDescription),
                collectibleMedias = collectibleMediaEntities?.let { collectibleMediaMapper(it) }.orEmpty(),
                description = description,
                traits = traitEntities?.let { collectibleTraitMapper(it) }.orEmpty()
            )
        }
    }

    private fun CollectibleResponse.isValid(): Boolean {
        return standard != null || mediaType != null || primaryImageUrl != null || title != null ||
            collection != null || !collectibleMedias.isNullOrEmpty()
    }
}
