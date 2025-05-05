package com.algorand.wallet.asset.domain.model

data class AssetCollectibleLiteQuery(
    val addresses: List<String>,
    val sortType: AssetCollectibleLiteSortType,
    val searchKeyword: String?,
    val filterOutZeroAmount: Boolean,
    val filterOutCollectibles: Boolean,
    val filterOutCollectiblesWithZeroAmount: Boolean,
)
