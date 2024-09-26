package com.algorand.android.module.asset.detail.component.asset.domain.usecase

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail

fun interface GetAssetDetail {
    suspend operator fun invoke(assetId: Long): AssetDetail?
}
