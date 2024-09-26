package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaTypeExtension
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity

internal interface CollectibleMediaTypeExtensionMapper {
    operator fun invoke(response: CollectibleMediaTypeExtensionResponse): CollectibleMediaTypeExtension
    operator fun invoke(entity: CollectibleMediaTypeExtensionEntity): CollectibleMediaTypeExtension
}
