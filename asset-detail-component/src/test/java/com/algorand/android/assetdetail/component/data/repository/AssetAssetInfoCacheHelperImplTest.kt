package com.algorand.android.module.asset.detail.component.data.repository

import com.algorand.android.module.asset.detail.component.data.mapper.entity.*
import com.algorand.android.module.asset.detail.component.data.mapper.model.AssetDetailMapper
import com.algorand.android.module.asset.detail.component.data.model.AssetDetailResponse
import com.algorand.android.module.asset.detail.component.domain.model.AssetDetail
import com.algorand.android.shared_db.assetdetail.dao.*
import com.algorand.android.shared_db.assetdetail.model.*
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class AssetAssetInfoCacheHelperImplTest {

    private val assetDetailDao: AssetDetailDao = mock()
    private val collectibleDao: CollectibleDao = mock()
    private val collectibleMediaDao: CollectibleMediaDao = mock()
    private val collectibleTraitDao: CollectibleTraitDao = mock()
    private val assetDetailMapper: AssetDetailMapper = mock()
    private val assetDetailEntityMapper: com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.AssetDetailEntityMapper =
        mock()
    private val collectibleEntityMapper: com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleEntityMapper =
        mock()
    private val collectibleMediaEntityMapper: com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaEntityMapper =
        mock()
    private val collectibleTraitEntityMapper: CollectibleTraitEntityMapper = mock()

    private val sut = AssetDetailCacheHelperImpl(
        assetDetailDao,
        collectibleDao,
        collectibleMediaDao,
        collectibleTraitDao,
        assetDetailMapper,
        assetDetailEntityMapper,
        collectibleEntityMapper,
        collectibleMediaEntityMapper,
        collectibleTraitEntityMapper
    )

    @Test
    fun `EXPECT asset details to be cached WHEN asset details are valid`() = runTest {
        val assetDetails = fixtureOf<List<AssetDetailResponse>>()
        val assetDetailEntities = fixtureOf<List<AssetDetailEntity>>()
        val collectibleEntities = fixtureOf<List<CollectibleEntity>>()
        val collectibleMediaEntities = fixtureOf<List<CollectibleMediaEntity>>()
        val collectibleTraitEntities = fixtureOf<List<CollectibleTraitEntity>>()

        whenever(assetDetailEntityMapper(assetDetails[0])).thenReturn(assetDetailEntities[0])
        whenever(collectibleEntityMapper(assetDetails[0])).thenReturn(collectibleEntities[0])
        whenever(collectibleMediaEntityMapper(assetDetails[0])).thenReturn(collectibleMediaEntities)
        whenever(collectibleTraitEntityMapper(assetDetails[0])).thenReturn(collectibleTraitEntities)

        sut.cacheAssetDetails(assetDetails)

        verify(assetDetailDao).insertAll(assetDetailEntities)
        verify(collectibleDao).insertAll(collectibleEntities)
        verify(collectibleMediaDao).insertAll(collectibleMediaEntities)
        verify(collectibleTraitDao).insertAll(collectibleTraitEntities)
    }

    @Test
    fun `EXPECT getAssetDetail to return asset by id WHEN asset detail cached before`() = runTest {
        whenever(assetDetailDao.getByAssetId(ASSET_ID)).thenReturn(ASSET_DETAIL_ENTITY)
        whenever(collectibleDao.getByCollectibleAssetId(ASSET_ID)).thenReturn(COLLECTIBLE_ENTITY)
        whenever(collectibleMediaDao.getByCollectibleAssetId(ASSET_ID)).thenReturn(COLLECTIBLE_MEDIA_ENTITIES)
        whenever(collectibleTraitDao.getByCollectibleAssetId(ASSET_ID)).thenReturn(COLLECTIBLE_TRAIT_ENTITIES)
        whenever(
            assetDetailMapper(
                ASSET_DETAIL_ENTITY,
                COLLECTIBLE_ENTITY,
                COLLECTIBLE_MEDIA_ENTITIES,
                COLLECTIBLE_TRAIT_ENTITIES
            )
        ).thenReturn(ASSET_DETAIL)

        val result = sut.getAssetDetail(ASSET_ID)

        assertEquals(ASSET_DETAIL, result)
    }

    @Test
    fun `EXPECT getAssetDetail to return null WHEN asset detail not cached before`() = runTest {
        whenever(assetDetailDao.getByAssetId(ASSET_ID)).thenReturn(null)

        val result = sut.getAssetDetail(ASSET_ID)

        assertNull(result)
    }

    @Test(timeout = 200L)
    fun `EXPECT queries to be triggered at the same time WHEN getAssetDetail is called`() = runTest {

        whenever(assetDetailDao.getByAssetId(ASSET_ID)).doAnswer { Thread.sleep(100); ASSET_DETAIL_ENTITY }
        whenever(collectibleDao.getByCollectibleAssetId(ASSET_ID)).doAnswer { Thread.sleep(100); COLLECTIBLE_ENTITY }
        whenever(collectibleMediaDao.getByCollectibleAssetId(ASSET_ID)).doAnswer {
            Thread.sleep(100); COLLECTIBLE_MEDIA_ENTITIES
        }
        whenever(collectibleTraitDao.getByCollectibleAssetId(ASSET_ID)).doAnswer {
            Thread.sleep(100); COLLECTIBLE_TRAIT_ENTITIES
        }
        whenever(
            assetDetailMapper(
                ASSET_DETAIL_ENTITY,
                COLLECTIBLE_ENTITY,
                COLLECTIBLE_MEDIA_ENTITIES,
                COLLECTIBLE_TRAIT_ENTITIES
            )
        ).thenReturn(ASSET_DETAIL)

        sut.getAssetDetail(ASSET_ID)

        verify(assetDetailDao).getByAssetId(ASSET_ID)
        verify(collectibleDao).getByCollectibleAssetId(ASSET_ID)
        verify(collectibleMediaDao).getByCollectibleAssetId(ASSET_ID)
        verify(collectibleTraitDao).getByCollectibleAssetId(ASSET_ID)
    }

    companion object {
        private const val ASSET_ID = 1L
        private val ASSET_DETAIL_ENTITY = fixtureOf<AssetDetailEntity>()
        private val COLLECTIBLE_ENTITY = fixtureOf<CollectibleEntity>()
        private val COLLECTIBLE_MEDIA_ENTITIES = fixtureOf<List<CollectibleMediaEntity>>()
        private val COLLECTIBLE_TRAIT_ENTITIES = fixtureOf<List<CollectibleTraitEntity>>()

        private val ASSET_DETAIL = fixtureOf<AssetDetail>()
    }
}
