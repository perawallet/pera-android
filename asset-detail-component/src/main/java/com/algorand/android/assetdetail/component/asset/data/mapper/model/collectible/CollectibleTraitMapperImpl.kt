package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleTrait
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity
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
