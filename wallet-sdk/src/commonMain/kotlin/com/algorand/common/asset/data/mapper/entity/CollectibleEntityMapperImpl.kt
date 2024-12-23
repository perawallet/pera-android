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

package com.algorand.common.asset.data.mapper.entity

import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.model.AssetResponse

internal class CollectibleEntityMapperImpl(
    private val collectibleStandardTypeEntityMapper: CollectibleStandardTypeEntityMapper,
    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper,
) : CollectibleEntityMapper {

    override fun invoke(response: AssetResponse): CollectibleEntity? {
        if (response.collectible == null) return null
        return CollectibleEntity(
            collectibleAssetId = response.assetId ?: return null,
            standardType = collectibleStandardTypeEntityMapper(response.collectible.standard),
            mediaType = collectibleMediaTypeEntityMapper(response.collectible.mediaType),
            primaryImageUrl = response.collectible.primaryImageUrl,
            title = response.collectible.title,
            description = response.collectible.description,
            collectionId = response.collectible.collection?.collectionId,
            collectionName = response.collectible.collection?.collectionName,
            collectionDescription = response.collectible.collection?.collectionDescription,
        )
    }
}
