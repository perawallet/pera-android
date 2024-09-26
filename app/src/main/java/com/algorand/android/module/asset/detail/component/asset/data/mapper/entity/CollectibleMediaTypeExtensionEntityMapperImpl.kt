package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity
import javax.inject.Inject

internal class CollectibleMediaTypeExtensionEntityMapperImpl @Inject constructor() :
    CollectibleMediaTypeExtensionEntityMapper {

    override fun invoke(response: CollectibleMediaTypeExtensionResponse?): CollectibleMediaTypeExtensionEntity {
        return when (response) {
            CollectibleMediaTypeExtensionResponse.GIF -> CollectibleMediaTypeExtensionEntity.GIF
            CollectibleMediaTypeExtensionResponse.WEBP -> CollectibleMediaTypeExtensionEntity.WEBP
            CollectibleMediaTypeExtensionResponse.UNKNOWN, null -> CollectibleMediaTypeExtensionEntity.UNKNOWN
        }
    }
}
