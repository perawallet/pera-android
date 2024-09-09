package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.AssetResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity

internal interface CollectibleEntityMapper {
    operator fun invoke(response: AssetResponse): CollectibleEntity?
}
