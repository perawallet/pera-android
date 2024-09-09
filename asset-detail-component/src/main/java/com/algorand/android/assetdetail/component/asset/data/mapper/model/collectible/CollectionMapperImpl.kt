package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectionResponse
import com.algorand.android.assetdetail.component.asset.domain.model.Collection
import javax.inject.Inject

internal class CollectionMapperImpl @Inject constructor() : CollectionMapper {

    override fun invoke(response: CollectionResponse): Collection? {
        with(response) {
            if (collectionId == null && collectionName.isNullOrBlank() && collectionDescription.isNullOrBlank()) {
                return null
            }
            return Collection(
                collectionId = collectionId,
                collectionName = collectionName,
                collectionDescription = collectionDescription
            )
        }
    }

    override fun invoke(
        collectionId: Long?,
        collectionName: String?,
        collectionDescription: String?
    ): Collection? {
        if (collectionId == null && collectionName.isNullOrBlank() && collectionDescription.isNullOrBlank()) return null
        return Collection(
            collectionId = collectionId,
            collectionName = collectionName,
            collectionDescription = collectionDescription
        )
    }
}
