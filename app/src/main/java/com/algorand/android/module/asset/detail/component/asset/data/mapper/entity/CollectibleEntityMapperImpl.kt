package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import javax.inject.Inject

internal class CollectibleEntityMapperImpl @Inject constructor(
    private val collectibleStandardTypeEntityMapper: CollectibleStandardTypeEntityMapper,
    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper,
) : CollectibleEntityMapper {

    override fun invoke(response: AssetResponse): CollectibleEntity? {
        if (response.collectible == null) return null
        return CollectibleEntity(
            collectibleAssetId = response.assetId ?: return null,
            standardType = collectibleStandardTypeEntityMapper(response.collectible.standard),
            mediaType = collectibleMediaTypeEntityMapper(response.collectible.mediaType),
            primaryImageUrl = response.collectible.primaryImageUrl,
            title = response.collectible.title,
            description = response.collectible.description,
            collectionId = response.collectible.collection?.collectionId,
            collectionName = response.collectible.collection?.collectionName,
            collectionDescription = response.collectible.collection?.collectionDescription,
        )
    }
}
