package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity
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
