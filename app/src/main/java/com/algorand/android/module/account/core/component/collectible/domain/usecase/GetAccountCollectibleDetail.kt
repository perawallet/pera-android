package com.algorand.android.module.account.core.component.collectible.domain.usecase

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData

interface GetAccountCollectibleDetail {
    suspend operator fun invoke(address: String, collectible: CollectibleDetail): BaseOwnedCollectibleData?
}
