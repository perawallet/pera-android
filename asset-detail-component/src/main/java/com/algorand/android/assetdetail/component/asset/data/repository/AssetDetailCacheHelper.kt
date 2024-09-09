package com.algorand.android.assetdetail.component.asset.data.repository

import com.algorand.android.assetdetail.component.asset.data.model.AssetResponse
import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail

internal interface AssetDetailCacheHelper {

    suspend fun cacheAssetDetails(assetDetails: List<AssetResponse>)

    suspend fun getAssetDetail(assetId: Long): AssetDetail?

    suspend fun getAsset(assetId: Long): Asset?

    suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail?

    suspend fun getCollectibleDetails(collectibleIds: List<Long>): List<CollectibleDetail>
}
