/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.foundation.cache.CacheResult
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class SingleAssetRepositoryImplTest {

    private val assetCache: SingleInMemoryLocalCache<Asset> = mockk(relaxed = true)
    private val assetDetailApi: AssetDetailApiService = mockk()
    private val assetMapper: AssetMapper = mockk()

    private val sut = SingleAssetRepositoryImpl(assetDetailApi, assetCache, assetMapper)

    @Test
    fun `EXPECT clear to be cached()`() = runTest {
        sut.clearCache()

        verify { assetCache.clear() }
    }

    @Test
    fun `EXPECT asset to be cached WHEN fetching succeeds`() = runTest {
        coEvery { assetDetailApi.getAssetDetail(ASSET_DETAIL.id) } returns Response.success(ASSET_RESPONSE)
        every { assetMapper(ASSET_RESPONSE) } returns ASSET_DETAIL
        val cacheSlot = slot<CacheResult.Success<Asset>>()
        every { assetCache.put(capture(cacheSlot)) } returns Unit

        sut.cacheAssetDetail(ASSET_DETAIL.id)

        val captured = cacheSlot.captured
        assertEquals(ASSET_DETAIL, captured.data)
    }

    @Test
    fun `EXPECT error to be cached WHEN fetching fails`() = runTest {
        val code = 404
        val errorBody = Response.error<AssetResponse>(code, "".toResponseBody(null))
        coEvery { assetDetailApi.getAssetDetail(ASSET_DETAIL.id) } returns errorBody
        val cacheSlot = slot<CacheResult<Asset>>()
        every { assetCache.put(capture(cacheSlot)) } returns Unit

        sut.cacheAssetDetail(ASSET_DETAIL.id)

        val captured = cacheSlot.captured
        assertTrue(captured is CacheResult.Error)
    }

    @Test
    fun `EXPECT nothing to be cached WHEN fetching succeeds but mapping fails`() = runTest {
        coEvery { assetDetailApi.getAssetDetail(ASSET_DETAIL.id) } returns Response.success(ASSET_RESPONSE)
        every { assetMapper(ASSET_RESPONSE) } returns null

        sut.cacheAssetDetail(ASSET_DETAIL.id)

        verify(exactly = 0) { assetCache.put(any()) }
    }

    @Test
    fun `EXPECT mapped asset detail flow WHEN cached data is not null`() {
        val cachedAsset = CacheResult.Success.create(ASSET_DETAIL)
        val assetCacheFlow = MutableStateFlow(cachedAsset)
        every { assetCache.cacheFlow } returns assetCacheFlow

        val testObserver = sut.getAssetDetailFlow().test()

        testObserver.assertValueHistory(ASSET_DETAIL)
    }

    @Test
    fun `EXPECT nothing to be emitted WHEN cache is empty`() {
        val testObserver = sut.getAssetDetailFlow().test()

        testObserver.assertValueHistory()
    }

    @Test
    fun `EXPECT nothing to be emitted WHEN cached data is null`() {
        val cacheFlow = MutableStateFlow<CacheResult<Asset>?>(null)
        every { assetCache.cacheFlow } returns cacheFlow

        val testObserver = sut.getAssetDetailFlow().test()

        testObserver.assertNoValue()
    }

    private companion object {
        val ASSET_DETAIL = peraFixture<Asset>()
        val ASSET_RESPONSE = peraFixture<AssetResponse>()
    }
}
