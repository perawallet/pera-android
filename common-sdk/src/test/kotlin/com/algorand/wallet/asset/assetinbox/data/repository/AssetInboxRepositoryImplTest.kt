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

package com.algorand.wallet.asset.assetinbox.data.repository

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.asset.assetinbox.data.mapper.AssetInboxRequestMapper
import com.algorand.wallet.asset.assetinbox.data.model.AssetInboxRequestsResponse
import com.algorand.wallet.asset.assetinbox.data.service.AssetInboxApiService
import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.InMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response

class AssetInboxRepositoryImplTest {

    private val assetInboxApiService: AssetInboxApiService = mockk()
    private val retrofitErrorHandler: PeraRetrofitErrorHandler = mockk()
    private val assetInboxRequestMapper: AssetInboxRequestMapper = mockk()
    private val inMemoryLocalCache: InMemoryLocalCache<String, AssetInboxRequest> = mockk(relaxed = true)

    private val assetInboxRepositoryImpl = AssetInboxRepositoryImpl(
        assetInboxApiService,
        retrofitErrorHandler,
        assetInboxRequestMapper,
        inMemoryLocalCache
    )

    @Test
    fun `EXPECT asset inbox request WHEN response is success`() = runTest {
        coEvery {
            assetInboxApiService.getAssetInboxAllAccountsRequests("address1,address2")
        } returns Response.success(ASSET_INBOX_RESPONSE)
        every { assetInboxRequestMapper(ASSET_INBOX_RESPONSE) } returns ASSET_INBOX

        val result = assetInboxRepositoryImpl.getRequests(ADDRESSES)

        val expected = PeraResult.Success(ASSET_INBOX)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT cache to be cleared WHEN clear cache invoked`() = runTest {
        assetInboxRepositoryImpl.clearCache()

        verify(exactly = 1) { inMemoryLocalCache.clear() }
    }

    @Test
    fun `EXPECT request count flow to be returned WHEN getRequestCountFlow invoked`() = runTest {
        val cacheMap = MutableStateFlow(hashMapOf(ADDRESS_1 to AssetInboxRequest(ADDRESS_1, 1)))
        every { inMemoryLocalCache.getCacheFlow() } returns cacheMap

        val result = assetInboxRepositoryImpl.getRequestCountFlow().test()
        cacheMap.value = hashMapOf(
            ADDRESS_1 to AssetInboxRequest(ADDRESS_1, 1),
            ADDRESS_2 to AssetInboxRequest(ADDRESS_2, 4)
        )

        result.assertValueHistory(1, 5)
    }

    @Test
    fun `EXPECT asset inbox requests to be cached WHEN cacheRequests invoked`() = runTest {
        val requests = listOf(
            AssetInboxRequest(ADDRESS_1, 1),
            AssetInboxRequest(ADDRESS_2, 4)
        )

        assetInboxRepositoryImpl.cacheRequests(requests)

        verify(exactly = 1) { inMemoryLocalCache.putAll(listOf(ADDRESS_1 to requests[0], ADDRESS_2 to requests[1])) }
    }

    @Test
    fun `EXPECT null WHEN getRequest is invoked but requested address is not in cache`() = runTest {
        every { inMemoryLocalCache[ADDRESS_1] } returns null

        val result = assetInboxRepositoryImpl.getRequest(ADDRESS_1)

        assertNull(result)
    }

    @Test
    fun `EXPECT request detail WHEN getRequest is invoked and requested address is in cache`() = runTest {
        val request = AssetInboxRequest(ADDRESS_1, 1)
        every { inMemoryLocalCache[ADDRESS_1] } returns request

        val result = assetInboxRepositoryImpl.getRequest(ADDRESS_1)

        assertEquals(request, result)
    }

    private companion object {
        const val ADDRESS_1 = "address1"
        const val ADDRESS_2 = "address2"
        val ADDRESSES = listOf(ADDRESS_1, ADDRESS_2)

        val ASSET_INBOX_RESPONSE = peraFixture<AssetInboxRequestsResponse>()
        val ASSET_INBOX = peraFixture<List<AssetInboxRequest>>()
    }
}
