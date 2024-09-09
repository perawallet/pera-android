package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.AssetResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity
import javax.inject.Inject

internal class CollectibleMediaEntityMapperImpl @Inject constructor(
    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper,
    private val collectibleMediaTypeExtensionEntityMapper: CollectibleMediaTypeExtensionEntityMapper
) : CollectibleMediaEntityMapper {

    override fun invoke(response: AssetResponse): List<CollectibleMediaEntity> {
        return response.collectible?.collectibleMedias?.mapNotNull {
            CollectibleMediaEntity(
                collectibleAssetId = response.assetId ?: return@mapNotNull null,
                mediaType = collectibleMediaTypeEntityMapper(it.mediaType),
                downloadUrl = it.downloadUrl,
                previewUrl = it.previewUrl,
                mediaTypeExtension = collectibleMediaTypeExtensionEntityMapper(it.mediaTypeExtension)
            )
        }.orEmpty()
    }
}