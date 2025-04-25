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

package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.model.AccountAssetsResponse
import com.algorand.wallet.account.info.data.model.AssetHoldingResponse
import com.algorand.wallet.account.info.data.service.AccountInformationApiService
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import kotlinx.coroutines.test.runTest

class AccountAssetHoldingsFetchHelperImplTest {

    private lateinit var sut: AccountAssetHoldingsFetchHelperImpl
    private val mockIndexerApi = mockk<AccountInformationApiService>()

    @Before
    fun setup() {
        sut = AccountAssetHoldingsFetchHelperImpl(mockIndexerApi)
    }

    @Test
    fun `EXPECT success result with assets WHEN api returns single page`() = runTest {
        val address = "TEST_ADDRESS"
        val mockAssets = listOf(
            mockk<AssetHoldingResponse>(),
            mockk<AssetHoldingResponse>()
        )
        val mockResponse = mockk<AccountAssetsResponse>()

        coEvery { mockResponse.assets } returns mockAssets
        coEvery { mockResponse.nextToken } returns null
        coEvery { mockIndexerApi.getAccountAssets(address, 5000, null) } returns Response.success(mockResponse)

        val result = sut.fetchAccountAssetHoldings(address)

        assertTrue(result is PeraResult.Success)
        assertEquals(mockAssets, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT success result with all assets WHEN api returns multiple pages`() = runTest {
        val address = "TEST_ADDRESS"
        val mockAssetsPage1 = listOf(
            mockk<AssetHoldingResponse>(),
            mockk<AssetHoldingResponse>()
        )
        val mockAssetsPage2 = listOf(
            mockk<AssetHoldingResponse>(),
            mockk<AssetHoldingResponse>()
        )
        val mockResponsePage1 = mockk<AccountAssetsResponse>()
        val mockResponsePage2 = mockk<AccountAssetsResponse>()
        val nextToken = "NEXT_TOKEN"

        coEvery { mockResponsePage1.assets } returns mockAssetsPage1
        coEvery { mockResponsePage1.nextToken } returns nextToken
        coEvery { mockResponsePage2.assets } returns mockAssetsPage2
        coEvery { mockResponsePage2.nextToken } returns null

        coEvery { mockIndexerApi.getAccountAssets(address, 5000, null) } returns Response.success(mockResponsePage1)
        coEvery { mockIndexerApi.getAccountAssets(address, 5000, nextToken) } returns Response.success(mockResponsePage2)

        val result = sut.fetchAccountAssetHoldings(address)

        assertTrue(result is PeraResult.Success)
        assertEquals(mockAssetsPage1 + mockAssetsPage2, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error result WHEN api throws exception`() = runTest {
        val address = "TEST_ADDRESS"
        val exception = IOException("Network error")

        coEvery { mockIndexerApi.getAccountAssets(address, 5000, null) } throws exception

        val result = sut.fetchAccountAssetHoldings(address)

        assertTrue(result is PeraResult.Error)
        assertTrue((result as PeraResult.Error).exception is IOException)
    }

    @Test
    fun `EXPECT error result WHEN api returns error response`() = runTest {
        val address = "TEST_ADDRESS"
        val errorCode = 404

        coEvery { mockIndexerApi.getAccountAssets(address, 5000, null) } returns
                Response.error(errorCode, "".toResponseBody("application/json".toMediaTypeOrNull()))

        val result = sut.fetchAccountAssetHoldings(address)

        assertTrue(result is PeraResult.Error)
        assertEquals(errorCode, (result as PeraResult.Error).code)
    }

    @Test
    fun `EXPECT success with empty list WHEN api returns empty assets list`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountAssetsResponse>()

        coEvery { mockResponse.assets } returns emptyList()
        coEvery { mockResponse.nextToken } returns "NEXT_TOKEN"
        coEvery { mockIndexerApi.getAccountAssets(address, 5000, null) } returns Response.success(mockResponse)

        val result = sut.fetchAccountAssetHoldings(address)

        assertTrue(result is PeraResult.Success)
        assertTrue((result as PeraResult.Success).data.isEmpty())
    }
}
