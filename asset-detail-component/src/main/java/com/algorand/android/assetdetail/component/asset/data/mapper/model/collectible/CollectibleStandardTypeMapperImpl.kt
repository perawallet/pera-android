package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleStandardType
import com.algorand.android.shared_db.assetdetail.model.CollectibleStandardTypeEntity
import javax.inject.Inject

internal class CollectibleStandardTypeMapperImpl @Inject constructor() : CollectibleStandardTypeMapper {

    override fun invoke(response: CollectibleStandardTypeResponse): CollectibleStandardType {
        return when (response) {
            CollectibleStandardTypeResponse.ARC_3 -> CollectibleStandardType.ARC_3
            CollectibleStandardTypeResponse.ARC_69 -> CollectibleStandardType.ARC_69
            CollectibleStandardTypeResponse.UNKNOWN -> CollectibleStandardType.UNKNOWN
        }
    }

    override fun invoke(entity: CollectibleStandardTypeEntity): CollectibleStandardType {
        return when (entity) {
            CollectibleStandardTypeEntity.ARC_3 -> CollectibleStandardType.ARC_3
            CollectibleStandardTypeEntity.ARC_69 -> CollectibleStandardType.ARC_69
            CollectibleStandardTypeEntity.UNKNOWN -> CollectibleStandardType.UNKNOWN
        }
    }
}
