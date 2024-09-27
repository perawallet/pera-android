package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity

internal interface CollectibleEntityMapper {
    operator fun invoke(response: AssetResponse): CollectibleEntity?
}
