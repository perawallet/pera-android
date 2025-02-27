/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.domain

import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.asset.domain.usecase.InitializeAssetsUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InitializeAssetsUseCaseTest {

    private val assetRepository: AssetRepository = mockk(relaxed = true)

    private val sut = InitializeAssetsUseCase(assetRepository)

    @Test
    fun `EXPECT cache to be cleared and assets to be fetched and cached`() = runTest {
        val assetIds = listOf(1L, 2L, 3L)

        sut(assetIds)

        coVerify { assetRepository.clearCache() }
        coVerify { assetRepository.fetchAndCacheAssets(listOf(1L, 2L, 3L), includeDeleted = false) }
    }
}
