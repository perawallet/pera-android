package com.algorand.android.assetdetail.component.asset.domain.usecase

import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail

fun interface GetAssetDetail {
    suspend operator fun invoke(assetId: Long): AssetDetail?
}
