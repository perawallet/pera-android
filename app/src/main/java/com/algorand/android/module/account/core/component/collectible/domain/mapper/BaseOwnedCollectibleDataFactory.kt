package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.accountinfo.component.domain.model.AssetHolding
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData

interface BaseOwnedCollectibleDataFactory {
    operator fun invoke(assetHolding: AssetHolding, collectibleDetail: CollectibleDetail): BaseOwnedCollectibleData
}
