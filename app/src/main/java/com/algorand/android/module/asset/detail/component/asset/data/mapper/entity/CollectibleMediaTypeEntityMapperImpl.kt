package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity
import javax.inject.Inject

internal class CollectibleMediaTypeEntityMapperImpl @Inject constructor() : CollectibleMediaTypeEntityMapper {

    override fun invoke(response: CollectibleMediaTypeResponse?): CollectibleMediaTypeEntity {
        return when (response) {
            CollectibleMediaTypeResponse.IMAGE -> CollectibleMediaTypeEntity.IMAGE
            CollectibleMediaTypeResponse.VIDEO -> CollectibleMediaTypeEntity.VIDEO
            CollectibleMediaTypeResponse.MIXED -> CollectibleMediaTypeEntity.MIXED
            CollectibleMediaTypeResponse.AUDIO -> CollectibleMediaTypeEntity.AUDIO
            CollectibleMediaTypeResponse.UNKNOWN, null -> CollectibleMediaTypeEntity.UNKNOWN
        }
    }
}
