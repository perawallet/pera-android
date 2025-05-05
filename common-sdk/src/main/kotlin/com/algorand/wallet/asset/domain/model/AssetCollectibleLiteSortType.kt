package com.algorand.wallet.asset.domain.model

sealed interface AssetCollectibleLiteSortType {

    data object NameAscending : AssetCollectibleLiteSortType

    data object NameDescending : AssetCollectibleLiteSortType

    data object ValueAscending : AssetCollectibleLiteSortType

    data object ValueDescending : AssetCollectibleLiteSortType
}
