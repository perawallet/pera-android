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

package com.algorand.wallet.asset.data.repository

import com.algorand.wallet.asset.data.database.dao.AssetDetailDao
import com.algorand.wallet.asset.data.database.dao.CollectibleDao
import com.algorand.wallet.asset.data.database.dao.CollectibleMediaDao
import com.algorand.wallet.asset.data.database.dao.CollectibleTraitDao
import com.algorand.wallet.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleDetailMapper
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.data.service.AssetDetailNodeApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetRepositoryImplTest {

    private val assetDetailApi: AssetDetailApiService = mockk(relaxed = true)
    private val assetDetailNodeApi: AssetDetailNodeApiService = mockk(relaxed = true)
    private val assetDetailCacheHelper: AssetDetailCacheHelper = mockk(relaxed = true)
    private val assetDetailDao: AssetDetailDao = mockk(relaxed = true)
    private val collectibleDao: CollectibleDao = mockk(relaxed = true)
    private val assetMapper: AssetMapper = mockk(relaxed = true)
    private val algoAssetDetailMapper: AlgoAssetDetailMapper = mockk(relaxed = true)
    private val collectibleDetailMapper: CollectibleDetailMapper = mockk(relaxed = true)
    private val collectibleMediaDao: CollectibleMediaDao = mockk(relaxed = true)
    private val collectibleTraitDao: CollectibleTraitDao = mockk(relaxed = true)

    private val sut = AssetRepositoryImpl(
        assetDetailApi,
        assetDetailNodeApi,
        assetDetailCacheHelper,
        assetDetailDao,
        collectibleDao,
        assetMapper,
        algoAssetDetailMapper,
        collectibleDetailMapper,
        collectibleMediaDao,
        collectibleTraitDao
    )

    @Test
    fun `EXPECT cached asset ids WHEN cache is not empty`() = runTest {
        coEvery { assetDetailDao.getAllIds() } returns listOf(1L, 2L, 3L)

        val result = sut.getCachedAssetIds()

        val expected = listOf(1L, 2L, 3L)
        assertEquals(expected, result)
    }
}
