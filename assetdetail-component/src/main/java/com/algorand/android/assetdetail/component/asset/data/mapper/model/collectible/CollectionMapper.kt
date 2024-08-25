package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectionResponse
import com.algorand.android.assetdetail.component.asset.domain.model.Collection

internal interface CollectionMapper {
    operator fun invoke(response: CollectionResponse): Collection?
    operator fun invoke(
        collectionId: Long?,
        collectionName: String?,
        collectionDescription: String?
    ): Collection?
}
