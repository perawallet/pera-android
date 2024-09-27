package com.algorand.android.module.asset.detail.component.collectible.data.mapper

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.shareddb.assetdetail.model.AssetDetailEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity

internal interface CollectibleDetailMapper {
    operator fun invoke(response: AssetResponse): CollectibleDetail?
    operator fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        mediaEntities: List<CollectibleMediaEntity>?,
        traitEntities: List<CollectibleTraitEntity>?
    ): CollectibleDetail?
}
