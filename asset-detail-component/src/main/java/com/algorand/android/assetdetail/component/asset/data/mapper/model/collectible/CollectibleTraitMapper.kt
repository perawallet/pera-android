package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleTrait
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity

internal interface CollectibleTraitMapper {
    operator fun invoke(response: CollectibleTraitResponse): CollectibleTrait?
    operator fun invoke(entities: List<CollectibleTraitEntity>): List<CollectibleTrait>
}
