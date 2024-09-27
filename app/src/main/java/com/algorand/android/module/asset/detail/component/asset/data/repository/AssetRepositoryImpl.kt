@file:Suppress("LongParameterList")
package com.algorand.android.module.asset.detail.component.asset.data.repository

import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetMapper
import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.data.service.AssetDetailApi
import com.algorand.android.module.asset.detail.component.asset.data.service.AssetDetailNodeApi
import com.algorand.android.module.asset.detail.component.asset.data.utils.toQueryString
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.repository.AssetRepository
import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.network.request
import com.algorand.android.module.shareddb.assetdetail.dao.AssetDetailDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleMediaDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleTraitDao
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

internal class AssetRepositoryImpl @Inject constructor(
    private val assetDetailApi: AssetDetailApi,
    private val assetDetailNodeApi: AssetDetailNodeApi,
    private val assetDetailCacheHelper: AssetDetailCacheHelper,
    private val assetDetailDao: AssetDetailDao,
    private val collectibleDao: CollectibleDao,
    private val assetMapper: AssetMapper,
    private val algoAssetDetailMapper: AlgoAssetDetailMapper,
    private val collectibleMediaDao: CollectibleMediaDao,
    private val collectibleTraitDao: CollectibleTraitDao,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AssetRepository {

    override suspend fun fetchAsset(assetId: Long): PeraResult<Asset> {
        return withContext(coroutineDispatcher) {
            try {
                val assetIds = listOf(assetId)
                val response = assetDetailApi.getAssetsByIds(assetIds.toQueryString())
                if (response.results.isEmpty()) {
                    PeraResult.Error(Exception("No asset found with id: $assetId"))
                } else {
                    mapAssetDetailResponseToResult(response.results.first())
                }
            } catch (exception: Exception) {
                PeraResult.Error(exception)
            }
        }
    }

    override suspend fun fetchAssets(assetIds: List<Long>): PeraResult<List<Asset>> {
        return try {
            withContext(coroutineDispatcher) {
                val chunkedAssetIds = assetIds.toSet().chunked(MAX_ASSET_FETCH_COUNT)
                val result = chunkedAssetIds.map {
                    async {
                        val response = assetDetailApi.getAssetsByIds(it.toQueryString())
                        response.results.mapNotNull { assetMapper(it) }
                    }
                }.awaitAll()
                PeraResult.Success(result.flatten())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun fetchAssetDetailFromNode(assetId: Long): PeraResult<AssetDetail> {
        return withContext(coroutineDispatcher) {
            request { assetDetailNodeApi.getAssetDetail(assetId) }.map {
                assetMapper(assetId, it)
            }
        }
    }

    override suspend fun fetchAndCacheAssets(assetIds: List<Long>, includeDeleted: Boolean): PeraResult<Unit> {
        return try {
            withContext(coroutineDispatcher) {
                val chunkedAssetIds = assetIds.toSet().chunked(MAX_ASSET_FETCH_COUNT)
                chunkedAssetIds.map {
                    async {
                        val response = assetDetailApi.getAssetsByIds(it.toQueryString(), includeDeleted)
                        assetDetailCacheHelper.cacheAssetDetails(response.results)
                    }
                }.awaitAll()
                PeraResult.Success(Unit)
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun getCollectiblesDetail(collectibleIds: List<Long>): List<CollectibleDetail> {
        return withContext(coroutineDispatcher) {
            assetDetailCacheHelper.getCollectibleDetails(collectibleIds)
        }
    }

    override suspend fun getAssetDetail(assetId: Long): AssetDetail? {
        return if (assetId == ALGO_ASSET_ID) {
            algoAssetDetailMapper()
        } else {
            assetDetailCacheHelper.getAssetDetail(assetId)
        }
    }

    override suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail? {
        return assetDetailCacheHelper.getCollectibleDetail(collectibleId)
    }

    override suspend fun getAsset(assetId: Long): Asset? {
        return if (assetId == ALGO_ASSET_ID) {
            algoAssetDetailMapper()
        } else {
            assetDetailCacheHelper.getAsset(assetId)
        }
    }

    override suspend fun clearCache() {
        withContext(coroutineDispatcher) {
            awaitAll(
                async { assetDetailDao.clearAll() },
                async { collectibleDao.clearAll() },
                async { collectibleMediaDao.clearAll() },
                async { collectibleTraitDao.clearAll() }
            )
        }
    }

    private fun mapAssetDetailResponseToResult(assetResponse: AssetResponse): PeraResult<Asset> {
        val assetDetail = assetMapper(assetResponse)
        return if (assetDetail == null) {
            PeraResult.Error(Exception("Failed to map asset detail"))
        } else {
            PeraResult.Success(assetDetail)
        }
    }

    companion object {
        private const val MAX_ASSET_FETCH_COUNT = 100
    }
}
