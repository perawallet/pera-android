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

package com.algorand.common.asset.data.repository

import com.algorand.common.asset.domain.util.AssetConstants
import com.algorand.common.asset.data.database.dao.AssetDetailDao
import com.algorand.common.asset.data.database.dao.CollectibleDao
import com.algorand.common.asset.data.database.dao.CollectibleMediaDao
import com.algorand.common.asset.data.database.dao.CollectibleTraitDao
import com.algorand.common.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.common.asset.data.mapper.model.AssetMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleDetailMapper
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.data.service.AssetDetailApiService
import com.algorand.common.asset.data.service.AssetDetailNodeApiService
import com.algorand.common.asset.data.utils.toQueryString
import com.algorand.common.asset.domain.model.Asset
import com.algorand.common.asset.domain.model.AssetDetail
import com.algorand.common.asset.domain.model.CollectibleDetail
import com.algorand.common.asset.domain.repository.AssetRepository
import com.algorand.common.foundation.PeraResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

internal class AssetRepositoryImpl(
    private val assetDetailApi: AssetDetailApiService,
    private val assetDetailNodeApi: AssetDetailNodeApiService,
    private val assetDetailCacheHelper: AssetDetailCacheHelper,
    private val assetDetailDao: AssetDetailDao,
    private val collectibleDao: CollectibleDao,
    private val assetMapper: AssetMapper,
    private val algoAssetDetailMapper: AlgoAssetDetailMapper,
    private val collectibleMediaDao: CollectibleMediaDao,
    private val collectibleTraitDao: CollectibleTraitDao,
    private val collectibleDetailMapper: CollectibleDetailMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AssetRepository {

    override suspend fun fetchAsset(assetId: Long): PeraResult<Asset> {
        return withContext(coroutineDispatcher) {
            try {
                val assetIds = listOf(assetId)
                assetDetailApi.getAssetsByIds(assetIds.toQueryString()).use(
                    onSuccess = { response ->
                        if (response.results.isEmpty()) {
                            PeraResult.Error(Exception("No asset found with id: $assetId"))
                        } else {
                            mapAssetDetailResponseToResult(response.results.first())
                        }
                    },
                    onFailed = { exception, code ->
                        PeraResult.Error(exception, code)
                    }
                )
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
                        response.getDataOrNull()?.results?.mapNotNull { assetMapper(it) }.orEmpty()
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
            assetDetailNodeApi.getAssetDetail(assetId).map {
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
                        val response = assetDetailApi.getAssetsByIds(it.toQueryString(), includeDeleted).getDataOrNull()
                        assetDetailCacheHelper.cacheAssetDetails(response?.results.orEmpty())
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

    override suspend fun fetchCollectibleDetail(collectibleAssetId: Long): PeraResult<CollectibleDetail> {
        return assetDetailApi.getAssetDetail(collectibleAssetId).use(
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

    override suspend fun getAssetDetail(assetId: Long): AssetDetail? {
        return if (assetId == AssetConstants.ALGO_ID) {
            algoAssetDetailMapper()
        } else {
            assetDetailCacheHelper.getAssetDetail(assetId)
        }
    }

    override suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail? {
        return assetDetailCacheHelper.getCollectibleDetail(collectibleId)
    }

    override suspend fun getAsset(assetId: Long): Asset? {
        return if (assetId == AssetConstants.ALGO_ID) {
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
