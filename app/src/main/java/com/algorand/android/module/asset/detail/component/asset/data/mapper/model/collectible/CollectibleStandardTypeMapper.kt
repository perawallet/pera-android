package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleStandardType
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleStandardTypeEntity

internal interface CollectibleStandardTypeMapper {
    operator fun invoke(response: CollectibleStandardTypeResponse): CollectibleStandardType
    operator fun invoke(entity: CollectibleStandardTypeEntity): CollectibleStandardType
}
