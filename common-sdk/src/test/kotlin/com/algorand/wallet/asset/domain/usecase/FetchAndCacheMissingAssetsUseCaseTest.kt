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

package com.algorand.wallet.asset.domain.usecase

import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class FetchAndCacheMissingAssetsUseCaseTest {

    private val assetRepository: AssetRepository = mockk(relaxed = true)

    private val sut = FetchAndCacheMissingAssetsUseCase(assetRepository)

    @Test
    fun `EXPECT success WHEN there are no missing assets`() = runTest {
        val assetIds = listOf(1L, 2L, 3L)
        coEvery { assetRepository.getCachedAssetIds() } returns assetIds

        val result = sut(assetIds, INCLUDE_DELETED)

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { assetRepository.fetchAndCacheAssets(any(), any()) }
    }

    @Test
    fun `EXPECT assets to be cached WHEN they are not in cache`() = runTest {
        val assetIds = listOf(1L, 2L, 3L)
        coEvery { assetRepository.getCachedAssetIds() } returns listOf(1L, 2L)
        coEvery {
            assetRepository.fetchAndCacheAssets(listOf(3L), INCLUDE_DELETED)
        } returns PeraResult.Success(Unit)

        val result = sut(assetIds, INCLUDE_DELETED)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `EXPECT failure WHEN there are missing assets but repository returns false`() = runTest {
        val assetIds = listOf(1L, 2L, 3L)
        coEvery { assetRepository.getCachedAssetIds() } returns listOf(1L, 2)
        coEvery {
            assetRepository.fetchAndCacheAssets(listOf(3L), INCLUDE_DELETED)
        } returns PeraResult.Error(Exception())

        val result = sut(assetIds, INCLUDE_DELETED)

        assertTrue(result.isFailed)
    }

    private companion object {
        const val INCLUDE_DELETED = false
    }
}
