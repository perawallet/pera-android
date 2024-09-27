@file:Suppress("LongParameterList")
package com.algorand.android.module.asset.detail.component.asset.data.repository

import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.AssetDetailEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleTraitEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetDetailMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetMapper
import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.CollectibleDetailMapper
import com.algorand.android.module.shareddb.assetdetail.dao.AssetDetailDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleMediaDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleTraitDao
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

internal class AssetDetailCacheHelperImpl @Inject constructor(
    private val assetDetailDao: AssetDetailDao,
    private val collectibleDao: CollectibleDao,
    private val collectibleMediaDao: CollectibleMediaDao,
    private val collectibleTraitDao: CollectibleTraitDao,
    private val assetMapper: AssetMapper,
    private val assetDetailMapper: AssetDetailMapper,
    private val collectibleDetailMapper: CollectibleDetailMapper,
    private val assetDetailEntityMapper: AssetDetailEntityMapper,
    private val collectibleEntityMapper: CollectibleEntityMapper,
    private val collectibleMediaEntityMapper: CollectibleMediaEntityMapper,
    private val collectibleTraitEntityMapper: CollectibleTraitEntityMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AssetDetailCacheHelper {

    override suspend fun cacheAssetDetails(assetDetails: List<AssetResponse>) {
        withContext(coroutineDispatcher) {
            val assetDetailEntities = assetDetails.mapNotNull { assetDetailEntityMapper(it) }
            val collectibleEntities = assetDetails.mapNotNull { collectibleEntityMapper(it) }
            val collectibleMediaEntities = assetDetails.map { collectibleMediaEntityMapper(it) }.flatten()
            val collectibleTraitEntities = assetDetails.map { collectibleTraitEntityMapper(it) }.flatten()

            collectibleDao.insertAll(collectibleEntities)
            collectibleMediaDao.insertAll(collectibleMediaEntities)
            assetDetailDao.insertAll(assetDetailEntities)
            collectibleTraitDao.insertAll(collectibleTraitEntities)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAssetDetail(assetId: Long): AssetDetail? {
        return withContext(coroutineDispatcher) {
            val deferredAssetDetail = async { assetDetailDao.getByAssetId(assetId) }
            val deferredCollectibleDetail = async { collectibleDao.getByCollectibleAssetId(assetId) }
            awaitAll(deferredAssetDetail, deferredCollectibleDetail)

            val assetDetail = deferredAssetDetail.getCompleted()
            val collectibleDetail = deferredCollectibleDetail.getCompleted()

            if (assetDetail == null || collectibleDetail != null) {
                return@withContext null
            }
            assetDetailMapper(assetDetail)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAsset(assetId: Long): Asset? {
        return withContext(coroutineDispatcher) {
            val deferredAssetDetailEntity = async { assetDetailDao.getByAssetId(assetId) }
            val deferredCollectibleEntity = async { collectibleDao.getByCollectibleAssetId(assetId) }
            val deferredCollectibleMediaEntities = async { collectibleMediaDao.getByCollectibleAssetId(assetId) }
            val deferredCollectibleTraits = async { collectibleTraitDao.getByCollectibleAssetId(assetId) }
            awaitAll(
                deferredAssetDetailEntity,
                deferredCollectibleEntity,
                deferredCollectibleMediaEntities,
                deferredCollectibleTraits
            )
            val assetDetailEntity = deferredAssetDetailEntity.getCompleted() ?: return@withContext null
            val collectibleEntity = deferredCollectibleEntity.getCompleted()
            val collectibleMediaEntities = deferredCollectibleMediaEntities.getCompleted()
            val collectibleTraitEntities = deferredCollectibleTraits.getCompleted()
            return@withContext assetMapper(
                assetDetailEntity,
                collectibleEntity,
                collectibleMediaEntities,
                collectibleTraitEntities
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail? {
        return withContext(coroutineDispatcher) {
            val deferredAssetDetailEntity = async { assetDetailDao.getByAssetId(collectibleId) }
            val deferredCollectibleEntity = async { collectibleDao.getByCollectibleAssetId(collectibleId) }
            val deferredCollectibleMediaEntities = async { collectibleMediaDao.getByCollectibleAssetId(collectibleId) }
            val deferredCollectibleTraits = async { collectibleTraitDao.getByCollectibleAssetId(collectibleId) }
            awaitAll(
                deferredAssetDetailEntity,
                deferredCollectibleEntity,
                deferredCollectibleMediaEntities,
                deferredCollectibleTraits
            )
            val assetDetailEntity = deferredAssetDetailEntity.getCompleted() ?: return@withContext null
            val collectibleEntity = deferredCollectibleEntity.getCompleted() ?: return@withContext null
            val collectibleMediaEntities = deferredCollectibleMediaEntities.getCompleted()
            val collectibleTraitEntities = deferredCollectibleTraits.getCompleted()
            return@withContext collectibleDetailMapper(
                assetDetailEntity,
                collectibleEntity,
                collectibleMediaEntities,
                collectibleTraitEntities
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCollectibleDetails(collectibleIds: List<Long>): List<CollectibleDetail> {
        return withContext(coroutineDispatcher) {
            val deferredAssetDetailEntity = async { assetDetailDao.getByAssetIds(collectibleIds) }
            val deferredCollectibleEntities = async { collectibleDao.getByCollectibleAssetIds(collectibleIds) }
            val deferredCollectibleTraits = async { collectibleTraitDao.getByCollectibleAssetIds(collectibleIds) }
            val deferredCollectibleMediaEntities = async {
                collectibleMediaDao.getByCollectibleAssetIds(collectibleIds)
            }
            awaitAll(
                deferredAssetDetailEntity,
                deferredCollectibleEntities,
                deferredCollectibleTraits,
                deferredCollectibleMediaEntities
            )

            val assetDetailEntities = deferredAssetDetailEntity.getCompleted()
            val collectibleEntities = deferredCollectibleEntities.getCompleted()
            val collectibleMediaEntities = deferredCollectibleMediaEntities.getCompleted()
            val collectibleTraitEntities = deferredCollectibleTraits.getCompleted()

            assetDetailEntities.mapNotNull { assetDetailEntity ->
                val collectible = collectibleEntities.firstOrNull {
                    it.collectibleAssetId == assetDetailEntity.assetId
                } ?: return@mapNotNull null
                val collectibleMedias = collectibleMediaEntities.filter {
                    it.collectibleAssetId == assetDetailEntity.assetId
                }
                val traits = collectibleTraitEntities.filter {
                    it.collectibleAssetId == assetDetailEntity.assetId
                }
                collectibleDetailMapper(assetDetailEntity, collectible, collectibleMedias, traits)
            }
        }
    }
}
