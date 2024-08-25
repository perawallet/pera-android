package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleResponse
import com.algorand.android.assetdetail.component.asset.domain.model.Collectible
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity

internal interface CollectibleMapper {
    operator fun invoke(response: CollectibleResponse): Collectible?
    operator fun invoke(
        entity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        traitEntities: List<CollectibleTraitEntity>?
    ): Collectible
}
