package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaTypeExtension
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeExtensionEntity
import javax.inject.Inject

internal class CollectibleMediaTypeExtensionMapperImpl @Inject constructor() : CollectibleMediaTypeExtensionMapper {

    override fun invoke(response: CollectibleMediaTypeExtensionResponse): CollectibleMediaTypeExtension {
        return when (response) {
            CollectibleMediaTypeExtensionResponse.GIF -> CollectibleMediaTypeExtension.GIF
            CollectibleMediaTypeExtensionResponse.WEBP -> CollectibleMediaTypeExtension.WEBP
            CollectibleMediaTypeExtensionResponse.UNKNOWN -> CollectibleMediaTypeExtension.UNKNOWN
        }
    }

    override fun invoke(entity: CollectibleMediaTypeExtensionEntity): CollectibleMediaTypeExtension {
        return when (entity) {
            CollectibleMediaTypeExtensionEntity.GIF -> CollectibleMediaTypeExtension.GIF
            CollectibleMediaTypeExtensionEntity.WEBP -> CollectibleMediaTypeExtension.WEBP
            CollectibleMediaTypeExtensionEntity.UNKNOWN -> CollectibleMediaTypeExtension.UNKNOWN
        }
    }
}
