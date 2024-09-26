package com.algorand.android.module.asset.detail.component.asset.data.repository

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail

internal interface AssetDetailCacheHelper {

    suspend fun cacheAssetDetails(assetDetails: List<AssetResponse>)

    suspend fun getAssetDetail(assetId: Long): AssetDetail?

    suspend fun getAsset(assetId: Long): Asset?

    suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail?

    suspend fun getCollectibleDetails(collectibleIds: List<Long>): List<CollectibleDetail>
}
