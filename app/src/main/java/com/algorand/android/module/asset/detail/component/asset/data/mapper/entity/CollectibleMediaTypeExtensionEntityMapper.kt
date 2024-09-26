package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity

internal interface CollectibleMediaTypeExtensionEntityMapper {
    operator fun invoke(response: CollectibleMediaTypeExtensionResponse?): CollectibleMediaTypeExtensionEntity
}
