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

import com.algorand.common.asset.data.database.model.CollectibleMediaTypeExtensionEntity
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.common.asset.domain.model.CollectibleMediaTypeExtension

internal class CollectibleMediaTypeExtensionMapperImpl : CollectibleMediaTypeExtensionMapper {

    override fun invoke(response: CollectibleMediaTypeExtensionResponse): CollectibleMediaTypeExtension {
        return when (response) {
            CollectibleMediaTypeExtensionResponse.GIF -> CollectibleMediaTypeExtension.GIF
            CollectibleMediaTypeExtensionResponse.WEBP -> CollectibleMediaTypeExtension.WEBP
            CollectibleMediaTypeExtensionResponse.UNKNOWN -> CollectibleMediaTypeExtension.UNKNOWN
        }
    }

    override fun invoke(entity: CollectibleMediaTypeExtensionEntity): CollectibleMediaTypeExtension {
        return when (entity) {
            CollectibleMediaTypeExtensionEntity.GIF -> CollectibleMediaTypeExtension.GIF
            CollectibleMediaTypeExtensionEntity.WEBP -> CollectibleMediaTypeExtension.WEBP
            CollectibleMediaTypeExtensionEntity.UNKNOWN -> CollectibleMediaTypeExtension.UNKNOWN
        }
    }
}
