package com.algorand.android.modules.accountsorting.domain.usecase

import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteSortType

fun interface GetAssetCollectibleLiteSortType {
    suspend operator fun invoke(): AssetCollectibleLiteSortType
}
