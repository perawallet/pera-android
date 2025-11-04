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

package com.algorand.wallet.asset.data.service

import com.algorand.test.peraFixture
import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.GetAssetsByIdsRequestBody
import com.algorand.wallet.foundation.network.model.Pagination
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultAssetDetailApiServiceTest {

    private val apiService: AssetDetailRetrofitApiService = mockk()
    private val errorLogger: PeraErrorLogger = mockk(relaxed = true)
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk()

    private val sut = DefaultAssetDetailApiService(apiService, errorLogger, isFeatureToggleEnabled)

    @Test
    fun `EXPECT getAssetDetail to use v1 endpoint WHEN device id is null`() = runTest {
        coEvery { apiService.getAssetDetail(ASSET_ID) } returns ASSET_RESPONSE

        val result = sut.getAssetDetail(ASSET_ID, deviceId = null)

        coVerify(exactly = 0) { apiService.getAssetDetailV2(any(), any()) }
        assertEquals(ASSET_RESPONSE, result)
    }

    @Test
    fun `EXPECT getAssetDetail to use v1 endpoint WHEN asset detail v2 feature toggle is disabled`() = runTest {
        coEvery { apiService.getAssetDetail(ASSET_ID) } returns ASSET_RESPONSE
        every { isFeatureToggleEnabled(ASSET_DETAIL_V2_FEATURE_TOGGLE) } returns false

        val result = sut.getAssetDetail(ASSET_ID, DEVICE_ID)

        coVerify(exactly = 0) { apiService.getAssetDetailV2(any(), any()) }
        assertEquals(ASSET_RESPONSE, result)
    }

    @Test
    fun `EXPECT getAssetDetail to use v1 endpoint and log error WHEN v2 endpoint throws an exception`() = runTest {
        val exception = Exception()
        coEvery { apiService.getAssetDetail(ASSET_ID) } returns ASSET_RESPONSE
        coEvery { apiService.getAssetDetailV2(DEVICE_ID_QUERY, ASSET_ID) } throws exception
        every { isFeatureToggleEnabled(ASSET_DETAIL_V2_FEATURE_TOGGLE) } returns true

        val result = sut.getAssetDetail(ASSET_ID, DEVICE_ID)

        assertEquals(ASSET_RESPONSE, result)
        verify { errorLogger.logError(exception) }
    }

    @Test
    fun `EXPECT getAssetDetail to use v2 endpoint WHEN device id is not null and asset detail v2 feature toggle is enabled`() =
        runTest {
            every { isFeatureToggleEnabled(ASSET_DETAIL_V2_FEATURE_TOGGLE) } returns true
            coEvery { apiService.getAssetDetailV2(DEVICE_ID_QUERY, ASSET_ID) } returns ASSET_RESPONSE

            val result = sut.getAssetDetail(ASSET_ID, DEVICE_ID)

            assertEquals(ASSET_RESPONSE, result)
        }

    @Test
    fun `EXPECT getAssetsById to use v1 endpoint WHEN device id is null`() = runTest {
        coEvery { apiService.getAssetsByIds(ASSET_IDS_QUERY, INCLUDE_DELETED) } returns ASSETS_PAGINATION_RESPONSE

        val result = sut.getAssetsByIds(ASSET_IDS, deviceId = null, INCLUDE_DELETED)

        coVerify(exactly = 0) { apiService.getAssetsByIdsV2(any()) }
        assertEquals(ASSETS_PAGINATION_RESPONSE, result)
    }

    @Test
    fun `EXPECT getAssetsById to use v2 endpoint WHEN device id is not null`() = runTest {
        coEvery { apiService.getAssetsByIdsV2(ASSETS_BY_ID_REQUEST_BODY) } returns ASSETS_PAGINATION_RESPONSE
        every { isFeatureToggleEnabled(ASSET_DETAIL_V2_FEATURE_TOGGLE) } returns true

        val result = sut.getAssetsByIds(ASSET_IDS, DEVICE_ID, INCLUDE_DELETED)

        coVerify(exactly = 0) { apiService.getAssetsByIds(ASSET_IDS_QUERY, INCLUDE_DELETED) }
        assertEquals(ASSETS_PAGINATION_RESPONSE, result)
    }

    @Test
    fun `EXPECT getAssetsById to use v1 endpoint and log error WHEN v2 endpoint throws an exception`() = runTest {
        val exception = Exception()
        coEvery { apiService.getAssetsByIds(ASSET_IDS_QUERY, INCLUDE_DELETED) } returns ASSETS_PAGINATION_RESPONSE
        coEvery { apiService.getAssetsByIdsV2(ASSETS_BY_ID_REQUEST_BODY) } throws exception
        every { isFeatureToggleEnabled(ASSET_DETAIL_V2_FEATURE_TOGGLE) } returns true

        val result = sut.getAssetsByIds(ASSET_IDS, DEVICE_ID, INCLUDE_DELETED)

        assertEquals(ASSETS_PAGINATION_RESPONSE, result)
        verify { errorLogger.logError(exception) }
    }

    private companion object {
        const val ASSET_IDS_QUERY = "1,2,3"
        const val DEVICE_ID = "12345"
        const val DEVICE_ID_QUERY = 12345L
        const val ASSET_ID = 1L
        val ASSET_IDS = listOf(1L, 2L, 3L)
        val INCLUDE_DELETED = peraFixture<Boolean>()
        val ASSET_RESPONSE = AssetResponse()
        val ASSETS_PAGINATION_RESPONSE = Pagination(next = null, results = listOf(ASSET_RESPONSE))
        val ASSETS_BY_ID_REQUEST_BODY = GetAssetsByIdsRequestBody(DEVICE_ID_QUERY, ASSET_IDS_QUERY, INCLUDE_DELETED)
        const val ASSET_DETAIL_V2_FEATURE_TOGGLE = "enable_asset_detail_v2_endpoint"
    }
}
