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

import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.model.AssetResponse

internal class CollectibleMediaEntityMapperImpl(
    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper,
    private val collectibleMediaTypeExtensionEntityMapper: CollectibleMediaTypeExtensionEntityMapper
) : CollectibleMediaEntityMapper {

    override fun invoke(response: AssetResponse): List<CollectibleMediaEntity> {
        return response.collectible?.collectibleMedias?.mapNotNull {
            CollectibleMediaEntity(
                collectibleAssetId = response.assetId ?: return@mapNotNull null,
                mediaType = collectibleMediaTypeEntityMapper(it.mediaType),
                downloadUrl = it.downloadUrl,
                previewUrl = it.previewUrl,
                mediaTypeExtension = collectibleMediaTypeExtensionEntityMapper(it.mediaTypeExtension)
            )
        }.orEmpty()
    }
}
