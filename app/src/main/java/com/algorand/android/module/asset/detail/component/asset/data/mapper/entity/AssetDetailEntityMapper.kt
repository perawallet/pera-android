package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.shared_db.assetdetail.model.AssetDetailEntity

internal interface AssetDetailEntityMapper {
    operator fun invoke(response: AssetResponse): AssetDetailEntity?
}
