package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteSortType
import javax.inject.Inject

internal class AssetCollectibleLiteSortTypeQueryMapperImpl @Inject constructor() :
    AssetCollectibleLiteSortTypeQueryMapper {

    override fun invoke(sortType: AssetCollectibleLiteSortType): String {
        return when (sortType) {
            AssetCollectibleLiteSortType.NameAscending -> "name_asc"
            AssetCollectibleLiteSortType.NameDescending -> "name_desc"
            AssetCollectibleLiteSortType.ValueAscending -> "value_asc"
            AssetCollectibleLiteSortType.ValueDescending -> "value_desc"
        }
    }
}
