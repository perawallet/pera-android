package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity

internal interface CollectibleMediaTypeEntityMapper {
    operator fun invoke(response: CollectibleMediaTypeResponse?): CollectibleMediaTypeEntity
}
