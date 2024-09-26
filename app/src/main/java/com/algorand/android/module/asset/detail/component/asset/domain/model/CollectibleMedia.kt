package com.algorand.android.module.asset.detail.component.asset.domain.model

data class CollectibleMedia(
    val mediaType: CollectibleMediaType?,
    val downloadUrl: String?,
    val previewUrl: String?,
    val mediaTypeExtension: CollectibleMediaTypeExtension?
)
