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

import com.algorand.common.asset.data.model.collectible.CollectionResponse
import com.algorand.common.asset.domain.model.Collection

internal class CollectionMapperImpl : CollectionMapper {

    override fun invoke(response: CollectionResponse): Collection? {
        with(response) {
            if (collectionId == null && collectionName.isNullOrBlank() && collectionDescription.isNullOrBlank()) {
                return null
            }
            return Collection(
                collectionId = collectionId,
                collectionName = collectionName,
                collectionDescription = collectionDescription
            )
        }
    }

    override fun invoke(
        collectionId: Long?,
        collectionName: String?,
        collectionDescription: String?
    ): Collection? {
        if (collectionId == null && collectionName.isNullOrBlank() && collectionDescription.isNullOrBlank()) return null
        return Collection(
            collectionId = collectionId,
            collectionName = collectionName,
            collectionDescription = collectionDescription
        )
    }
}
