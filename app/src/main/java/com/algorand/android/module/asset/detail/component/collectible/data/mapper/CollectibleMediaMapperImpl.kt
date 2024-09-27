package com.algorand.android.module.asset.detail.component.collectible.data.mapper

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.AUDIO
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.IMAGE
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.UNKNOWN
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.VIDEO
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia.AudioCollectibleMedia
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia.GifCollectibleMedia
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia.ImageCollectibleMedia
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia.UnsupportedCollectibleMedia
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia.VideoCollectibleMedia
import javax.inject.Inject

internal class CollectibleMediaMapperImpl @Inject constructor() : CollectibleMediaMapper {

    override fun invoke(response: CollectibleMediaResponse): BaseCollectibleMedia {
        return when (response.mediaType) {
            IMAGE -> getCollectibleMediaForImage(response)
            VIDEO -> mapToVideoCollectibleMedia(response)
            AUDIO -> mapToAudioCollectibleMedia(response)
            UNKNOWN -> mapToUnsupportedCollectibleMedia(response)
            else -> mapToUnsupportedCollectibleMedia(response)
        }
    }

    private fun getCollectibleMediaForImage(response: CollectibleMediaResponse): BaseCollectibleMedia {
        return when (response.mediaTypeExtension) {
            CollectibleMediaTypeExtensionResponse.GIF -> mapToGifCollectibleMedia(response)
            CollectibleMediaTypeExtensionResponse.WEBP -> mapToImageCollectibleMedia(response)
            else -> mapToImageCollectibleMedia(response)
        }
    }

    private fun mapToImageCollectibleMedia(response: CollectibleMediaResponse): ImageCollectibleMedia {
        return ImageCollectibleMedia(
            downloadUrl = response.downloadUrl,
            previewUrl = response.previewUrl
        )
    }

    private fun mapToGifCollectibleMedia(response: CollectibleMediaResponse): GifCollectibleMedia {
        return GifCollectibleMedia(
            downloadUrl = response.downloadUrl,
            previewUrl = response.previewUrl
        )
    }

    private fun mapToVideoCollectibleMedia(response: CollectibleMediaResponse): VideoCollectibleMedia {
        return VideoCollectibleMedia(
            downloadUrl = response.downloadUrl,
            previewUrl = response.previewUrl
        )
    }

    private fun mapToAudioCollectibleMedia(response: CollectibleMediaResponse): AudioCollectibleMedia {
        return AudioCollectibleMedia(
            downloadUrl = response.downloadUrl,
            previewUrl = response.previewUrl
        )
    }

    private fun mapToUnsupportedCollectibleMedia(response: CollectibleMediaResponse): UnsupportedCollectibleMedia {
        return UnsupportedCollectibleMedia(
            downloadUrl = response.downloadUrl,
            previewUrl = response.previewUrl
        )
    }
}
