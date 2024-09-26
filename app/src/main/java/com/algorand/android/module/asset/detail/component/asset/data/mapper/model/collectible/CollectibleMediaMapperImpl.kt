package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMedia
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity
import javax.inject.Inject

internal class CollectibleMediaMapperImpl @Inject constructor(
    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper,
    private val collectibleMediaTypeExtensionMapper: CollectibleMediaTypeExtensionMapper
) : CollectibleMediaMapper {

    override fun invoke(response: CollectibleMediaResponse): CollectibleMedia? {
        return with(response) {
            if (mediaType == null && downloadUrl == null && previewUrl == null && mediaTypeExtension == null) {
                return null
            }
            CollectibleMedia(
                mediaType = mediaType?.let { collectibleMediaTypeMapper(it) },
                downloadUrl = downloadUrl,
                previewUrl = previewUrl,
                mediaTypeExtension = mediaTypeExtension?.let { collectibleMediaTypeExtensionMapper(it) }
            )
        }
    }

    override fun invoke(entities: List<CollectibleMediaEntity>): List<CollectibleMedia> {
        return entities.map { entity ->
            with(entity) {
                CollectibleMedia(
                    mediaType = collectibleMediaTypeMapper(mediaType),
                    downloadUrl = downloadUrl,
                    previewUrl = previewUrl,
                    mediaTypeExtension = collectibleMediaTypeExtensionMapper(mediaTypeExtension)
                )
            }
        }
    }
}
