package com.algorand.android.core.component.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.core.component.domain.model.BaseAccountAssetData

internal interface PendingRemovalAssetDataMapper {
    operator fun invoke(asset: Asset): BaseAccountAssetData.PendingAssetData.DeletionAssetData
}
