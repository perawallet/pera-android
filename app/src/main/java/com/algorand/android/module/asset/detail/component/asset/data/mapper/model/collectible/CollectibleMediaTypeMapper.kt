package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaType
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity

internal interface CollectibleMediaTypeMapper {
    operator fun invoke(response: CollectibleMediaTypeResponse): CollectibleMediaType
    operator fun invoke(entity: CollectibleMediaTypeEntity): CollectibleMediaType
}
