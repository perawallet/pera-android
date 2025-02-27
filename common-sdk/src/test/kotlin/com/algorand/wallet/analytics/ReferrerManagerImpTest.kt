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

package com.algorand.wallet.analytics.data

import android.util.Log
import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.utils.UrlReferrerParser
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ReferrerManagerImplTest {

    private lateinit var installReferralApiClient: InstallReferrerApiClient
    private lateinit var referrerRepository: ReferrerRepository
    private lateinit var urlReferrerParser: UrlReferrerParser
    private lateinit var coroutineDispatcher: CoroutineDispatcher
    private lateinit var sut: ReferrerManagerImpl
    private lateinit var installReferrerClient: InstallReferrerClient
    private lateinit var referrerDetails: ReferrerDetails

    @Before
    fun setUp() {
        installReferralApiClient = mockk()
        referrerRepository = mockk()
        urlReferrerParser = mockk()
        coroutineDispatcher = StandardTestDispatcher()
        sut = ReferrerManagerImpl(installReferralApiClient, referrerRepository, urlReferrerParser, coroutineDispatcher)
        installReferrerClient = mockk()
        referrerDetails = mockk()

        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        every { installReferralApiClient.initialize() } returns installReferrerClient
        every { installReferrerClient.endConnection() } returns Unit
        every { installReferrerClient.installReferrer } returns referrerDetails
    }

    @Test
    fun `initialize should handle FEATURE_NOT_SUPPORTED response`() = runTest {
        every { installReferrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerSetupFinished(InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED)
        }

        sut.initialize()

        coVerify(exactly = 0) { referrerRepository.saveReferrerData(any()) }
    }

    @Test
    fun `initialize should handle SERVICE_UNAVAILABLE response`() = runTest {
        every { installReferrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerSetupFinished(InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE)
        }

        sut.initialize()

        coVerify(exactly = 0) { referrerRepository.saveReferrerData(any()) }
    }

    @Test
    fun `initialize should handle service disconnected`() = runTest {
        every { installReferrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerServiceDisconnected()
        }

        sut.initialize()

        coVerify(exactly = 0) { referrerRepository.saveReferrerData(any()) }
    }
}
