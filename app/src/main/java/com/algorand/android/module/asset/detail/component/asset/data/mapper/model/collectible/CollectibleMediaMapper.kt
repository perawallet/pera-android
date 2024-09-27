package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMedia
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity

internal interface CollectibleMediaMapper {
    operator fun invoke(response: CollectibleMediaResponse): CollectibleMedia?
    operator fun invoke(entities: List<CollectibleMediaEntity>): List<CollectibleMedia>
}
