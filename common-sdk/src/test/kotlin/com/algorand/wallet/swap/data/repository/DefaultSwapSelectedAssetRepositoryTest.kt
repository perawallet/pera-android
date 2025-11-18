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

package com.algorand.wallet.swap.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.swap.data.dao.SwapSelectedAssetDao
import com.algorand.wallet.swap.data.mapper.SwapSelectedAssetDetailMapper
import com.algorand.wallet.swap.data.model.SwapSelectedAssetDto
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultSwapSelectedAssetRepositoryTest {

    private val swapSelectedAssetDao: SwapSelectedAssetDao = mockk(relaxed = true)
    private val selectedAssetDetailMapper: SwapSelectedAssetDetailMapper = mockk()

    private val sut = DefaultSwapSelectedAssetRepository(swapSelectedAssetDao, selectedAssetDetailMapper)

    @Test
    fun `EXPECT null WHEN asset is not found`(): TestResult = runTest {
        coEvery { swapSelectedAssetDao.getAssetWithHolding(ADDRESS, ASSET_ID) } returns null

        val result = sut.getSelectedAssetDetails(ADDRESS, ASSET_ID)

        assertNull(result)
    }

    @Test
    fun `EPXECT mapped asset detail WHEN asset is found`(): TestResult = runTest {
        coEvery { swapSelectedAssetDao.getAssetWithHolding(ADDRESS, ASSET_ID) } returns ASSET_DTO
        coEvery { selectedAssetDetailMapper(ASSET_DTO) } returns ASSET_DETAIL

        val result = sut.getSelectedAssetDetails(ADDRESS, ASSET_ID)

        assertEquals(ASSET_DETAIL, result)
    }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val ASSET_ID = peraFixture<Long>()
        val ASSET_DETAIL = peraFixture<SwapSelectedAssetDetail>()
        val ASSET_DTO = peraFixture<SwapSelectedAssetDto>()
    }
}
