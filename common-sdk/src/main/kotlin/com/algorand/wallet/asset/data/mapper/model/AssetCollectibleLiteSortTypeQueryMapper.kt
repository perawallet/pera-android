package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteSortType

internal fun interface AssetCollectibleLiteSortTypeQueryMapper {
    operator fun invoke(sortType: AssetCollectibleLiteSortType): String
}
