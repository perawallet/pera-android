package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectionResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.Collection

internal interface CollectionMapper {
    operator fun invoke(response: CollectionResponse): Collection?
    operator fun invoke(
        collectionId: Long?,
        collectionName: String?,
        collectionDescription: String?
    ): Collection?
}
