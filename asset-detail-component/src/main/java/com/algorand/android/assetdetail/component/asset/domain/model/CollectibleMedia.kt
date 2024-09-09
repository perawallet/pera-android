package com.algorand.android.assetdetail.component.asset.domain.model

data class CollectibleMedia(
    val mediaType: CollectibleMediaType?,
    val downloadUrl: String?,
    val previewUrl: String?,
    val mediaTypeExtension: CollectibleMediaTypeExtension?
)
