package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleTrait
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity

internal interface CollectibleTraitMapper {
    operator fun invoke(response: CollectibleTraitResponse): CollectibleTrait?
    operator fun invoke(entities: List<CollectibleTraitEntity>): List<CollectibleTrait>
}
