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

package com.algorand.wallet.asset.assetinbox.domain

import androidx.lifecycle.testing.TestLifecycleOwner
import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.model.AccountCacheStatus
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.wallet.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import com.algorand.wallet.asset.assetinbox.domain.usecase.CacheAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.ClearAssetInboxCache
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxValidAddresses
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AssetInboxCacheManagerImplTest {

    private val cacheManager: LifecycleAwareCacheManager = mockk(relaxed = true)
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow = mockk()
    private val getAssetInboxRequests: GetAssetInboxRequests = mockk()
    private val cacheAssetInboxRequests: CacheAssetInboxRequests = mockk(relaxed = true)
    private val clearAssetInboxCache: ClearAssetInboxCache = mockk(relaxed = true)
    private val getAssetInboxValidAddresses: GetAssetInboxValidAddresses = mockk()
    private val getAllAccountInformationFlow: GetAllAccountInformationFlow = mockk()

    private val sut = AssetInboxCacheManagerImpl(
        cacheManager,
        getAccountDetailCacheStatusFlow,
        getAssetInboxRequests,
        cacheAssetInboxRequests,
        clearAssetInboxCache,
        getAssetInboxValidAddresses,
        getAllAccountInformationFlow
    )

    @Test
    fun `EXPECT cache manager to be initialized and listener to be set WHEN initialize is invoked`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()

        sut.initialize(lifecycleOwner.lifecycle)

        verify { cacheManager.setListener(sut) }
        verify { lifecycleOwner.lifecycle.addObserver(cacheManager) }
    }

    @Test
    fun `EXPECT manager to run WHEN account cache is initialized`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getAccountDetailCacheStatusFlow() } returns flowOf(
            AccountCacheStatus.IDLE,
            AccountCacheStatus.LOADING,
            AccountCacheStatus.INITIALIZED
        )
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onInitializeManager(this)

        verify(exactly = 1) { cacheManager.stopCurrentJob() }
        verify(exactly = 1) { cacheManager.startJob() }
    }

    @Test
    fun `EXPECT cache to be updated WHEN manager job runs and api call succeeds`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getAccountDetailCacheStatusFlow() } returns flowOf(AccountCacheStatus.INITIALIZED)
        coEvery { getAssetInboxValidAddresses() } returns listOf("address")
        coEvery { getAssetInboxRequests(listOf("address")) } returns PeraResult.Success(ASSET_INBOX_REQUESTS)
        every { getAllAccountInformationFlow() } returns flowOf(mapOf("address" to ACCOUNT_INFO))
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onStartJob(this)

        coVerify { cacheAssetInboxRequests(ASSET_INBOX_REQUESTS) }
    }

    @Test
    fun `EXPECT cache to be cleared WHEN manager job runs and api call fails`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getAccountDetailCacheStatusFlow() } returns flowOf(AccountCacheStatus.INITIALIZED)
        coEvery { getAssetInboxValidAddresses() } returns listOf("address")
        coEvery { getAssetInboxRequests(listOf("address")) } returns PeraResult.Error(Exception())
        every { getAllAccountInformationFlow() } returns flowOf(mapOf("address" to ACCOUNT_INFO))
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onStartJob(this)

        coVerify { clearAssetInboxCache() }
    }

    @Test
    fun `EXPECT cache to be cleared WHEN manager job starts`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getAccountDetailCacheStatusFlow() } returns flowOf(AccountCacheStatus.INITIALIZED)
        coEvery { getAssetInboxValidAddresses() } returns listOf("address")
        coEvery { getAssetInboxRequests(listOf("address")) } returns PeraResult.Error(Exception())
        every { getAllAccountInformationFlow() } returns flowOf(mapOf("address" to ACCOUNT_INFO))
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onStartJob(this)

        coVerify { clearAssetInboxCache() }
    }

    private companion object {
        val ASSET_INBOX_REQUESTS = peraFixture<List<AssetInboxRequest>>()
        val ACCOUNT_INFO = peraFixture<AccountInformation>()
    }
}
