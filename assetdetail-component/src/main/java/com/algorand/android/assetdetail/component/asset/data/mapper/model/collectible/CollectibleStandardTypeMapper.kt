package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleStandardType
import com.algorand.android.shared_db.assetdetail.model.CollectibleStandardTypeEntity

internal interface CollectibleStandardTypeMapper {
    operator fun invoke(response: CollectibleStandardTypeResponse): CollectibleStandardType
    operator fun invoke(entity: CollectibleStandardTypeEntity): CollectibleStandardType
}
