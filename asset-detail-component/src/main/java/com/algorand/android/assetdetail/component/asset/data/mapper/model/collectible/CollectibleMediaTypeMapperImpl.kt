package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleMediaType
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity
import javax.inject.Inject

internal class CollectibleMediaTypeMapperImpl @Inject constructor() : CollectibleMediaTypeMapper {

    override fun invoke(response: CollectibleMediaTypeResponse): CollectibleMediaType {
        return when (response) {
            CollectibleMediaTypeResponse.IMAGE -> CollectibleMediaType.IMAGE
            CollectibleMediaTypeResponse.VIDEO -> CollectibleMediaType.VIDEO
            CollectibleMediaTypeResponse.MIXED -> CollectibleMediaType.MIXED
            CollectibleMediaTypeResponse.AUDIO -> CollectibleMediaType.AUDIO
            CollectibleMediaTypeResponse.UNKNOWN -> CollectibleMediaType.UNKNOWN
        }
    }

    override fun invoke(entity: CollectibleMediaTypeEntity): CollectibleMediaType {
        return when (entity) {
            CollectibleMediaTypeEntity.IMAGE -> CollectibleMediaType.IMAGE
            CollectibleMediaTypeEntity.VIDEO -> CollectibleMediaType.VIDEO
            CollectibleMediaTypeEntity.MIXED -> CollectibleMediaType.MIXED
            CollectibleMediaTypeEntity.AUDIO -> CollectibleMediaType.AUDIO
            CollectibleMediaTypeEntity.UNKNOWN -> CollectibleMediaType.UNKNOWN
        }
    }
}
