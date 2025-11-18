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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import com.algorand.wallet.swap.domain.repository.SwapSelectedAssetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSelectedSwapAssetDetailUseCaseTest {

    private val fetchAndCacheAssets: FetchAndCacheAssets = mockk(relaxed = true)
    private val selectedAssetDetailRepository: SwapSelectedAssetRepository = mockk()

    private val sut = GetSelectedSwapAssetDetailUseCase(fetchAndCacheAssets, selectedAssetDetailRepository)

    @Test
    fun `EXPECT cached asset detail WHEN exists`(): TestResult = runTest {
        coEvery { selectedAssetDetailRepository.getSelectedAssetDetails(ADDRESS, ASSET_ID) } returns ASSET_DETAIL

        val result = sut(ADDRESS, ASSET_ID)

        val expected = PeraResult.Success(ASSET_DETAIL)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT asset detail to be cached WHEN not exists`(): TestResult = runTest {
        coEvery {
            selectedAssetDetailRepository.getSelectedAssetDetails(ADDRESS, ASSET_ID)
        } returnsMany listOf(null, ASSET_DETAIL)

        val result = sut(ADDRESS, ASSET_ID)

        val expected = PeraResult.Success(ASSET_DETAIL)
        assertEquals(expected, result)
        coVerify { fetchAndCacheAssets(listOf(ASSET_ID), includeDeleted = true) }
    }

    @Test
    fun `EXPECT error WHEN asset detail not found after fetching`(): TestResult = runTest {
        coEvery { selectedAssetDetailRepository.getSelectedAssetDetails(ADDRESS, ASSET_ID) } returns null

        val result = sut(ADDRESS, ASSET_ID)

        assert(result is PeraResult.Error)
        coVerify { fetchAndCacheAssets(listOf(ASSET_ID), includeDeleted = true) }
    }

    private companion object {
        const val ADDRESS = "address"
        const val ASSET_ID = 12345L
        val ASSET_DETAIL = peraFixture<SwapSelectedAssetDetail>()
    }
}
