package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleStandardTypeEntity
import javax.inject.Inject

internal class CollectibleStandardTypeEntityMapperImpl @Inject constructor() : CollectibleStandardTypeEntityMapper {

    override fun invoke(response: CollectibleStandardTypeResponse?): CollectibleStandardTypeEntity {
        return when (response) {
            CollectibleStandardTypeResponse.ARC_3 -> CollectibleStandardTypeEntity.ARC_3
            CollectibleStandardTypeResponse.ARC_69 -> CollectibleStandardTypeEntity.ARC_69
            CollectibleStandardTypeResponse.UNKNOWN, null -> CollectibleStandardTypeEntity.UNKNOWN
        }
    }
}
