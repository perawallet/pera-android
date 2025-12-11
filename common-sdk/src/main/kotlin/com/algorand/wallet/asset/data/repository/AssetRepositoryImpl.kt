/*
 * Copyright 2022-2025 Pera Wallet, LDA
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

import com.algorand.wallet.asset.data.database.dao.AssetDetailDao
import com.algorand.wallet.asset.data.database.dao.CollectibleDao
import com.algorand.wallet.asset.data.database.dao.CollectibleMediaDao
import com.algorand.wallet.asset.data.database.dao.CollectibleTraitDao
import com.algorand.wallet.asset.data.database.model.AssetLiteInformationDao
import com.algorand.wallet.asset.data.mapper.entity.AlgoAssetDetailEntityMapper
import com.algorand.wallet.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleDetailMapper
import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.SetAssetFavoriteStatusRequestBody
import com.algorand.wallet.asset.data.model.SetAssetPriceAlertStatusRequestBody
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.data.service.AssetDetailNodeApiService
import com.algorand.wallet.asset.data.service.AssetStatusApiService
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.AssetDetail
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.asset.lite.domain.model.AssetLiteInformation
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.utils.request
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

internal class AssetRepositoryImpl @Inject constructor(
    private val assetDetailApi: AssetDetailApiService,
    private val assetDetailNodeApi: AssetDetailNodeApiService,
    private val assetStatusApiService: AssetStatusApiService,
    private val assetDetailCacheHelper: AssetDetailCacheHelper,
    private val assetDetailDao: AssetDetailDao,
    private val collectibleDao: CollectibleDao,
    private val assetMapper: AssetMapper,
    private val algoAssetDetailMapper: AlgoAssetDetailMapper,
    private val collectibleDetailMapper: CollectibleDetailMapper,
    private val collectibleMediaDao: CollectibleMediaDao,
    private val collectibleTraitDao: CollectibleTraitDao,
    private val algoAssetDetailEntityMapper: AlgoAssetDetailEntityMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AssetRepository {

    override suspend fun fetchAsset(assetId: Long, deviceId: String?): PeraResult<Asset> {
        return withContext(coroutineDispatcher) {
            try {
                val response = assetDetailApi.getAssetDetail(assetId, deviceId)
                mapAssetDetailResponseToResult(response)
            } catch (exception: Exception) {
                PeraResult.Error(exception)
            }
        }
    }

    override suspend fun fetchAssets(assetIds: List<Long>, deviceId: String?): PeraResult<List<Asset>> {
        return try {
            withContext(coroutineDispatcher) {
                val chunkedAssetIds = assetIds.toSet().chunked(MAX_ASSET_FETCH_COUNT)
                val result = chunkedAssetIds.map {
                    async {
                        val response = assetDetailApi.getAssetsByIds(assetIds, deviceId, includeDeleted = null)
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

    override suspend fun fetchAndCacheAssets(
        assetIds: List<Long>,
        deviceId: String?,
        includeDeleted: Boolean
    ): PeraResult<Unit> {
        return try {
            withContext(coroutineDispatcher) {
                val chunkedAssetIds = mutableListOf(ALGO_ID).apply {
                    addAll(assetIds)
                }.toSet().chunked(MAX_ASSET_FETCH_COUNT)
                chunkedAssetIds.map {
                    async {
                        val response = assetDetailApi.getAssetsByIds(it, deviceId, includeDeleted)
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
        val cachedAssetDetail = assetDetailCacheHelper.getAssetDetail(assetId)
        if (cachedAssetDetail == null && assetId == ALGO_ID) return algoAssetDetailMapper()
        return cachedAssetDetail
    }

    override suspend fun getAssetsDetail(assetIds: List<Long>): List<AssetDetail> {
        val assetDetails = assetDetailCacheHelper.getAssetsDetail(assetIds)
        return if (assetIds.any { it == ALGO_ID } && assetDetails.any { it.id != ALGO_ID }) {
            assetDetails + algoAssetDetailMapper()
        } else {
            assetDetails
        }
    }

    override suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail? {
        return assetDetailCacheHelper.getCollectibleDetail(collectibleId)
    }

    override suspend fun getAsset(assetId: Long): Asset? {
        return assetDetailCacheHelper.getAsset(assetId)
    }

    override suspend fun fetchCollectibleDetail(
        collectibleAssetId: Long,
        deviceId: String?
    ): PeraResult<CollectibleDetail> {
        return try {
            val response = assetDetailApi.getAssetDetail(collectibleAssetId, deviceId)
            val collectibleDetail = collectibleDetailMapper(response)
            if (collectibleDetail == null) {
                PeraResult.Error(Exception("CollectibleDetail is null"))
            } else {
                PeraResult.Success(collectibleDetail)
            }
        } catch (e: Exception) {
            PeraResult.Error(e)
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

    override suspend fun getCachedAssetIds(): List<Long> {
        return withContext(coroutineDispatcher) {
            assetDetailDao.getAllIds()
        }
    }

    override suspend fun isCollectibleExist(collectibleId: Long): Boolean {
        return assetDetailCacheHelper.isCollectibleExist(collectibleId)
    }

    override fun getAssetsLiteInformationFlow(
        assetIds: List<Long>
    ): Flow<Map<Long, AssetLiteInformation?>> {
        if (assetIds.isEmpty()) return flowOf(emptyMap())

        val chunkFlows = assetIds
            .chunked(MAX_ASSET_CHUNK_SIZE)
            .map { chunk ->
                assetDetailDao
                    .getLiteInformationByAssetIds(chunk)
                    .distinctUntilChanged()
            }

        return combine(chunkFlows) { assetDaoLists: Array<List<AssetLiteInformationDao>> ->
            val fullAssetMap = mutableMapOf<Long, AssetLiteInformation?>()
            assetDaoLists.forEach { assetDaoList ->
                assetDaoList.forEach { assetDao ->
                    fullAssetMap[assetDao.id] = AssetLiteInformation(
                        id = assetDao.id,
                        usdValue = assetDao.usdValue,
                        decimals = assetDao.decimals
                    )
                }
            }

            fullAssetMap
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getAssetLiteInformation(assetId: Long): AssetLiteInformation? {
        return assetDetailDao.getLiteInformation(assetId)?.let {
            AssetLiteInformation(it.id, it.usdValue, it.decimals)
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

    override suspend fun getAssetCreatorAddress(assetId: Long): String? {
        return withContext(coroutineDispatcher) {
            assetDetailDao.getAssetCreatorAddress(assetId)
        }
    }

    override suspend fun cacheAlgoAssetDetail(usdValue: BigDecimal?) {
        withContext(coroutineDispatcher) {
            val algoDetail = assetDetailDao.getByAssetId(ALGO_ID)
            val entity = algoDetail?.copy(usdValue = usdValue) ?: algoAssetDetailEntityMapper(usdValue)
            assetDetailDao.insert(entity)
        }
    }

    override suspend fun getRecentlyAddedCollectibleUrls(count: Int): List<String> {
        return collectibleDao.getRecentlyAddedCollectibleUrls(count)
    }

    override suspend fun setFavoriteStatus(assetId: Long, deviceId: String, isFavorite: Boolean): PeraResult<Unit> {
        return try {
            val body = SetAssetFavoriteStatusRequestBody(deviceId.toLong(), isFavorite)
            assetStatusApiService.setAssetFavoriteStatus(assetId, body)
            assetDetailDao.updateFavoriteStatus(assetId, isFavorite)
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun setPriceAlertStatus(assetId: Long, deviceId: String, enabled: Boolean): PeraResult<Unit> {
        return try {
            val body = SetAssetPriceAlertStatusRequestBody(deviceId.toLong(), enabled)
            assetStatusApiService.setAssetPriceAlertStatus(assetId, body)
            assetDetailDao.updatePriceAlertStatus(assetId, enabled)
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun getFavoriteStatuses(assetIds: List<Long>): Map<Long, Boolean?> {
        return assetDetailDao.getFavoriteStatuses(assetIds)
    }

    companion object {
        private const val MAX_ASSET_FETCH_COUNT = 100
        private const val MAX_ASSET_CHUNK_SIZE = 900
    }
}
