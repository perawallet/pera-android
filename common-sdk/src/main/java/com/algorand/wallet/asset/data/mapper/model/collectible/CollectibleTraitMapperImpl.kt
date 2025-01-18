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

import com.algorand.wallet.asset.data.database.model.CollectibleTraitEntity
import com.algorand.wallet.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.wallet.asset.domain.model.CollectibleTrait
import javax.inject.Inject

internal class CollectibleTraitMapperImpl @Inject constructor() : CollectibleTraitMapper {

    override fun invoke(response: CollectibleTraitResponse): CollectibleTrait? {
        return with(response) {
            if (name.isNullOrBlank() && value.isNullOrBlank()) {
                return null
            }
            CollectibleTrait(name = name, value = value)
        }
    }

    override fun invoke(entities: List<CollectibleTraitEntity>): List<CollectibleTrait> {
        return entities.map { entity ->
            CollectibleTrait(
                name = entity.displayName,
                value = entity.displayValue
            )
        }
    }
}
