package com.algorand.android.core.component.collectible.domain.usecase

import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData

interface GetAccountCollectibleDetail {
    suspend operator fun invoke(address: String, collectible: CollectibleDetail): BaseOwnedCollectibleData?
}
