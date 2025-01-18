/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.data.repository

import com.algorand.wallet.asset.data.utils.toQueryString
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.AssetDetail
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.asset.data.database.dao.AssetDetailDao
import com.algorand.wallet.asset.data.database.dao.CollectibleDao
import com.algorand.wallet.asset.data.database.dao.CollectibleMediaDao
import com.algorand.wallet.asset.data.database.dao.CollectibleTraitDao
import com.algorand.wallet.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleDetailMapper
import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.data.service.AssetDetailNodeApiService
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.utils.request
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

internal class AssetRepositoryImpl @Inject constructor(
    private val assetDetailApi: AssetDetailApiService,
    private val assetDetailNodeApi: AssetDetailNodeApiService,
    private val assetDetailCacheHelper: AssetDetailCacheHelper,
    private val assetDetailDao: AssetDetailDao,
    private val collectibleDao: CollectibleDao,
    private val assetMapper: AssetMapper,
    private val algoAssetDetailMapper: AlgoAssetDetailMapper,
    private val collectibleDetailMapper: CollectibleDetailMapper,
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
        return if (assetId == ALGO_ID) {
            algoAssetDetailMapper()
        } else {
            assetDetailCacheHelper.getAssetDetail(assetId)
        }
    }

    override suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail? {
        return assetDetailCacheHelper.getCollectibleDetail(collectibleId)
    }

    override suspend fun getAsset(assetId: Long): Asset? {
        return if (assetId == ALGO_ID) {
            algoAssetDetailMapper()
        } else {
            assetDetailCacheHelper.getAsset(assetId)
        }
    }

    override suspend fun fetchCollectibleDetail(collectibleAssetId: Long): PeraResult<CollectibleDetail> {
        return request { assetDetailApi.getAssetDetail(collectibleAssetId) }.use(
            onSuccess = {
                val collectibleDetail = collectibleDetailMapper(it)
                if (collectibleDetail == null) {
                    PeraResult.Error(Exception("CollectibleDetail is null"))
                } else {
                    PeraResult.Success(collectibleDetail)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
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
