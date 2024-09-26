package com.algorand.android.module.asset.detail.component.data.repository

import com.algorand.android.module.asset.detail.component.data.mapper.model.AssetDetailMapper
import com.algorand.android.module.asset.detail.component.data.model.*
import com.algorand.android.module.asset.detail.component.data.service.AssetDetailApi
import com.algorand.android.shared_db.assetdetail.dao.*
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class AssetAssetInfoRepositoryImplTest {

    private val assetDetailApi: AssetDetailApi = mock {
        onBlocking { getAssetsByIds("123") } doReturn PAGINATED_ASSET_DETAIL_RESPONSE
    }
    private val assetDetailMapper: AssetDetailMapper = mock {
        on { invoke(ASSET_DETAIL_RESPONSE) } doReturn ASSET_DETAIL
    }

    private val assetDetailCacheHelper: AssetDetailCacheHelper = mock()

    private val assetDetailDao: AssetDetailDao = mock()
    private val collectibleDao: CollectibleDao = mock()
    private val collectibleMediaDao: CollectibleMediaDao = mock()
    private val collectibleTraitDao: CollectibleTraitDao = mock()

    private val sut = AssetDetailRepositoryImpl(
        assetDetailApi,
        assetDetailMapper,
        assetDetailCacheHelper,
        assetDetailDao,
        collectibleDao,
        collectibleMediaDao,
        collectibleTraitDao
    )

    @Test
    fun `EXPECT asset detail WHEN fetch asset detail api result is successful`() = runTest {
        val result = sut.fetchAssetDetail(ASSET_ID)

        assertEquals(Result.success(ASSET_DETAIL), result)
    }

    @Test
    fun `EXPECT failure WHEN fetch asset detail response list is empty`() = runTest {
        val paginatedAssetDetailResponse = Pagination<AssetDetailResponse>(null, emptyList())
        whenever(assetDetailApi.getAssetsByIds("123")).thenReturn(paginatedAssetDetailResponse)

        val result = sut.fetchAssetDetail(ASSET_ID)

        assertTrue(result.isFailure)
    }

    @Test
    fun `EXPECT failure WHEN fetch asset detail mapping returns null`() = runTest {
        whenever(assetDetailMapper(ASSET_DETAIL_RESPONSE)).thenReturn(null)

        val result = sut.fetchAssetDetail(ASSET_ID)

        assertTrue(result.isFailure)
    }

    @Test
    fun `EXPECT failure WHEN fetch asset detail api call throws exception`() = runTest {
        whenever(assetDetailApi.getAssetsByIds("123")).doAnswer { throw Exception() }

        val result = sut.fetchAssetDetail(ASSET_ID)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test
    fun `EXPECT asset detail WHEN getAssetDetails is called and asset was cached before`() = runTest {
        whenever(assetDetailCacheHelper.getAssetDetail(ASSET_ID)).thenReturn(ASSET_DETAIL)

        val result = sut.getAssetDetail(ASSET_ID)

        assertSame(ASSET_DETAIL, result)
    }

    @Test(timeout = 200L)
    fun `EXPECT assets to be fetched as chunks WHEN there are more than 100 assets`() = runTest {
        val assetIds = (1..500L).toList()
        val assetDetailResponseList = (1..100L).map {
            ASSET_DETAIL_RESPONSE.copy(assetId = it)
        }
        whenever(assetDetailMapper(any())).thenReturn(ASSET_DETAIL)
        whenever(assetDetailApi.getAssetsByIds(any(), eq(null))).doAnswer {
            Thread.sleep(100L)
            Pagination(null, assetDetailResponseList)
        }

        sut.fetchAndCacheAssetDetails(assetIds, false)

        verify(assetDetailApi, times(5)).getAssetsByIds(any(), eq(null))
        verify(assetDetailCacheHelper, times(5)).cacheAssetDetails(assetDetailResponseList)
    }

    @Test
    fun `EXPECT failure WHEN fetch and cache asset details api call throws exception`() = runTest {
        whenever(assetDetailApi.getAssetsByIds(any(), eq(null))).doAnswer { throw Exception() }

        val result = sut.fetchAndCacheAssetDetails(listOf(ASSET_ID), false)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test(timeout = 200L)
    fun `EXPECT all cache to be cleared at the same time WHEN clearCache is called`() = runTest {
        whenever(assetDetailDao.clearAll()).doAnswer { Thread.sleep(100L) }
        whenever(collectibleDao.clearAll()).doAnswer { Thread.sleep(100L) }
        whenever(collectibleMediaDao.clearAll()).doAnswer { Thread.sleep(100L) }
        whenever(collectibleTraitDao.clearAll()).doAnswer { Thread.sleep(100L) }

        sut.clearCache()
    }

    companion object {
        private const val ASSET_ID = 123L

        private val ASSET_DETAIL_RESPONSE = fixtureOf<AssetDetailResponse>()
        private val PAGINATED_ASSET_DETAIL_RESPONSE = Pagination(null, listOf(ASSET_DETAIL_RESPONSE))
        private val ASSET_DETAIL = fixtureOf<com.algorand.android.module.asset.detail.component.domain.model.AssetDetail>()
    }
}
