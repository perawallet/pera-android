package com.algorand.android.assetdetail.component.asset.domain.model

data class Collectible(
    val standardType: CollectibleStandardType?,
    val mediaType: CollectibleMediaType?,
    val primaryImageUrl: String?,
    val title: String?,
    val collection: Collection?,
    val collectibleMedias: List<CollectibleMedia>,
    val description: String?,
    val traits: List<CollectibleTrait>
)
