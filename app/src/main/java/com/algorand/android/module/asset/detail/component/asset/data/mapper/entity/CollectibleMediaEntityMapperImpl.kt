package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
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
