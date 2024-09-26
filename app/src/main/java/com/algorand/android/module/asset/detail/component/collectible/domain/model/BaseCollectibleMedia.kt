package com.algorand.android.module.asset.detail.component.collectible.domain.model

sealed class BaseCollectibleMedia {

    abstract val downloadUrl: String?
    abstract val previewUrl: String?

    data class ImageCollectibleMedia(
        override val downloadUrl: String?,
        override val previewUrl: String?
    ) : BaseCollectibleMedia()

    data class GifCollectibleMedia(
        override val downloadUrl: String?,
        override val previewUrl: String?
    ) : BaseCollectibleMedia()

    data class VideoCollectibleMedia(
        override val downloadUrl: String?,
        override val previewUrl: String?
    ) : BaseCollectibleMedia()

    data class AudioCollectibleMedia(
        override val downloadUrl: String?,
        override val previewUrl: String?
    ) : BaseCollectibleMedia()

    data class UnsupportedCollectibleMedia(
        override val downloadUrl: String?,
        override val previewUrl: String?
    ) : BaseCollectibleMedia()

    data class NoMediaCollectibleMedia(
        override val downloadUrl: String?,
        override val previewUrl: String?
    ) : BaseCollectibleMedia()
}
