package com.algorand.android.module.account.core.component.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset

internal interface PendingRemovalAssetDataMapper {
    operator fun invoke(asset: Asset): BaseAccountAssetData.PendingAssetData.DeletionAssetData
}
