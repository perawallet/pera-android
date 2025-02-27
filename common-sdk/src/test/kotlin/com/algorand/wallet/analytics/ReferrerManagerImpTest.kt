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
import com.algorand.wallet.analytics.domain.usecases.model.ReferralData
import com.algorand.wallet.analytics.domain.utils.UrlReferrerParser
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ReferrerManagerImplTest {
    private val mockInstallReferralApiClient = mockk<InstallReferrerApiClient>()
    private val mockReferrerRepository = mockk<ReferrerRepository>()
    private val mockUrlQueryParser = mockk<UrlReferrerParser>()
    private val mockInstallReferrerClient = mockk<InstallReferrerClient>()
    private val mockReferrerDetails = mockk<ReferrerDetails>()

    private lateinit var listenerSlot: CapturingSlot<InstallReferrerStateListener>

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var sut: ReferrerManagerImpl

    @Before
    fun setup() {
        sut = ReferrerManagerImpl(
            mockInstallReferralApiClient,
            mockReferrerRepository,
            mockUrlQueryParser,
            testDispatcher
        )

        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        listenerSlot = slot()

        every { mockInstallReferralApiClient.initialize() } returns mockInstallReferrerClient
        every { mockInstallReferrerClient.startConnection(capture(listenerSlot)) } just Runs
        every { mockInstallReferrerClient.endConnection() } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initialize should start connection with referrer client`() = runTest(testDispatcher) {
        sut.initialize()

        verify { mockInstallReferralApiClient.initialize() }
        verify { mockInstallReferrerClient.startConnection(any()) }
    }

    @Test
    fun `when referrer setup is OK should process and save referrer data`() = runTest(testDispatcher) {
        val referrerUrl = "utm_source=google&utm_medium=cpc"
        val referrerData = mockk<ReferralData>() // Mock the ReferralData object

        every { mockInstallReferrerClient.installReferrer } returns mockReferrerDetails
        every { mockReferrerDetails.installReferrer } returns referrerUrl
        every { mockUrlQueryParser.getReferrerData(referrerUrl) } returns referrerData
        coEvery { mockReferrerRepository.saveReferrerData(referrerData) } just Runs

        sut.initialize()

        listenerSlot.captured.onInstallReferrerSetupFinished(InstallReferrerClient.InstallReferrerResponse.OK)

        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockInstallReferrerClient.installReferrer }
        verify { mockReferrerDetails.installReferrer }
        verify { mockUrlQueryParser.getReferrerData(referrerUrl) }
        coVerify { mockReferrerRepository.saveReferrerData(referrerData) }
        verify { mockInstallReferrerClient.endConnection() }
        verify { Log.i("InstallReferrer", "Referrer URL: $referrerUrl") }
    }

    @Test
    fun `when feature not supported should log and end connection`() = runTest(testDispatcher) {
        sut.initialize()

        listenerSlot.captured.onInstallReferrerSetupFinished(
            InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED
        )

        verify { Log.i("InstallReferrer", "Feature not supported on this device") }
        verify { mockInstallReferrerClient.endConnection() }
        verify(exactly = 0) { mockInstallReferrerClient.installReferrer }
    }

    @Test
    fun `when service unavailable should log and end connection`() = runTest(testDispatcher) {
        sut.initialize()

        listenerSlot.captured.onInstallReferrerSetupFinished(
            InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE
        )

        verify { Log.i("InstallReferrer", "Referrer service unavailable") }
        verify { mockInstallReferrerClient.endConnection() }
        verify(exactly = 0) { mockInstallReferrerClient.installReferrer }
    }

    @Test
    fun `when service disconnected should log the event`() = runTest(testDispatcher) {
        sut.initialize()

        listenerSlot.captured.onInstallReferrerServiceDisconnected()

        verify { Log.i("InstallReferrer", "Referrer service disconnected") }
    }
}
