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

import com.algorand.common.asset.data.database.model.CollectibleMediaTypeEntity
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeResponse

internal class CollectibleMediaTypeEntityMapperImpl : CollectibleMediaTypeEntityMapper {

    override fun invoke(response: CollectibleMediaTypeResponse?): CollectibleMediaTypeEntity {
        return when (response) {
            CollectibleMediaTypeResponse.IMAGE -> CollectibleMediaTypeEntity.IMAGE
            CollectibleMediaTypeResponse.VIDEO -> CollectibleMediaTypeEntity.VIDEO
            CollectibleMediaTypeResponse.MIXED -> CollectibleMediaTypeEntity.MIXED
            CollectibleMediaTypeResponse.AUDIO -> CollectibleMediaTypeEntity.AUDIO
            CollectibleMediaTypeResponse.UNKNOWN, null -> CollectibleMediaTypeEntity.UNKNOWN
        }
    }
}
