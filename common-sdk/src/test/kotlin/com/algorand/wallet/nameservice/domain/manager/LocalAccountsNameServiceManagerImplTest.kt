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

package com.algorand.wallet.nameservice.domain.manager

import androidx.lifecycle.testing.TestLifecycleOwner
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.analytics.domain.model.FirebaseTokenStatus
import com.algorand.wallet.analytics.domain.usecase.GetFirebaseTokenStatusFlow
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import com.algorand.wallet.nameservice.domain.usecase.InitializeAccountNameService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LocalAccountsNameServiceManagerImplTest {

    private val cacheManager: LifecycleAwareCacheManager = mockk(relaxed = true)
    private val getFirebaseTokenStatusFlow: GetFirebaseTokenStatusFlow = mockk()
    private val getLocalAccountCountFlow: GetLocalAccountCountFlow = mockk()
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk()
    private val initializeAccountNameService: InitializeAccountNameService = mockk(relaxed = true)

    private val sut = LocalAccountsNameServiceManagerImpl(
        cacheManager,
        getFirebaseTokenStatusFlow,
        getLocalAccountCountFlow,
        initializeAccountNameService,
        getLocalAccountsAddresses
    )

    @Test
    fun `EXPECT cache manager to be initialized and listener to be set WHEN initialize is invoked`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()

        sut.initialize(lifecycleOwner.lifecycle)

        verify { cacheManager.setListener(sut) }
        verify { lifecycleOwner.lifecycle.addObserver(cacheManager) }
    }

    @Test
    fun `EXPECT manager to run WHEN there is account and token is ready`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getLocalAccountCountFlow() } returns flowOf(1)
        every { getFirebaseTokenStatusFlow() } returns flowOf(FirebaseTokenStatus.Success)
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onInitializeManager(this)

        verify { cacheManager.stopCurrentJob() }
        verify { cacheManager.startJob() }
    }

    @Test
    fun `EXPECT nothing WHEN there is account but token is not ready`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getLocalAccountCountFlow() } returns flowOf(1)
        every { getFirebaseTokenStatusFlow() } returns flowOf(FirebaseTokenStatus.Loading)
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onInitializeManager(this)

        verify(exactly = 0) { cacheManager.startJob() }
    }

    @Test
    fun `EXPECT nothing WHEN there is no account but token is ready`() = runTest {
        val lifecycleOwner = TestLifecycleOwner()
        every { getLocalAccountCountFlow() } returns flowOf(0)
        every { getFirebaseTokenStatusFlow() } returns flowOf(FirebaseTokenStatus.Success)
        sut.initialize(lifecycleOwner.lifecycle)

        sut.onInitializeManager(this)

        verify(exactly = 0) { cacheManager.startJob() }
    }

    @Test
    fun `EXPECT name service cache to be updated WHEN manager job is run`() = runTest {
        coEvery { getLocalAccountsAddresses() } returns listOf("address1", "address2")

        sut.onStartJob(this)

        coVerify { initializeAccountNameService(listOf("address1", "address2")) }
    }
}
