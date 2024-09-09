package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleStandardTypeEntity

internal interface CollectibleStandardTypeEntityMapper {
    operator fun invoke(response: CollectibleStandardTypeResponse?): CollectibleStandardTypeEntity
}
