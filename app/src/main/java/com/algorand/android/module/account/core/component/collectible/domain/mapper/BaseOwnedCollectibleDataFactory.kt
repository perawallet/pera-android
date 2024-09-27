package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail

interface BaseOwnedCollectibleDataFactory {
    operator fun invoke(assetHolding: AssetHolding, collectibleDetail: CollectibleDetail): BaseOwnedCollectibleData
}
