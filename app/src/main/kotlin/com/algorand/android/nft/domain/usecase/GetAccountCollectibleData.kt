package com.algorand.android.nft.domain.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData

fun interface GetAccountCollectibleData {
    suspend operator fun invoke(address: String, assetId: Long): BaseOwnedCollectibleData?
}
