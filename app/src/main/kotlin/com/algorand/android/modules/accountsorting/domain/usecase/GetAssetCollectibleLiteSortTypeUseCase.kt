package com.algorand.android.modules.accountsorting.domain.usecase

import com.algorand.android.modules.sorting.assetsorting.domain.model.AssetSortPreference
import com.algorand.android.modules.sorting.assetsorting.domain.usecase.AssetSortTypeUseCase
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteSortType
import javax.inject.Inject

internal class GetAssetCollectibleLiteSortTypeUseCase @Inject constructor(
    private val assetSortTypeUseCase: AssetSortTypeUseCase
) : GetAssetCollectibleLiteSortType {

    override suspend fun invoke(): AssetCollectibleLiteSortType {
        return assetSortTypeUseCase.getSortPreferenceType().let { sortType ->
            when (sortType) {
                AssetSortPreference.ALPHABETICALLY_ASCENDING -> AssetCollectibleLiteSortType.NameAscending
                AssetSortPreference.ALPHABETICALLY_DESCENDING -> AssetCollectibleLiteSortType.NameDescending
                AssetSortPreference.BALANCE_ASCENDING -> AssetCollectibleLiteSortType.ValueAscending
                AssetSortPreference.BALANCE_DESCENDING -> AssetCollectibleLiteSortType.ValueDescending
            }
        }
    }
}
