package com.algorand.android.assetdetail.component.asset.domain.repository

import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.foundation.PeraResult

internal interface AssetRepository {

    suspend fun fetchAsset(assetId: Long): PeraResult<Asset>

    suspend fun fetchAssets(assetIds: List<Long>): PeraResult<List<Asset>>

    suspend fun fetchAssetDetailFromNode(assetId: Long): PeraResult<AssetDetail>

    suspend fun fetchAndCacheAssets(assetIds: List<Long>, includeDeleted: Boolean): PeraResult<Unit>

    suspend fun getAssetDetail(assetId: Long): AssetDetail?

    suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail?

    suspend fun getAsset(assetId: Long): Asset?

    suspend fun clearCache()

    suspend fun getCollectiblesDetail(collectibleIds: List<Long>): List<CollectibleDetail>
}
