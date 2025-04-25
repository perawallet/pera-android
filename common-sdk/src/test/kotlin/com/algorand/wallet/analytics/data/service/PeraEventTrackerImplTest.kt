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

package com.algorand.wallet.analytics.data.service

import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.analytics.domain.usecase.GetReferrerData
import com.algorand.wallet.analytics.domain.usecase.IsStrongBoxUsedForEncryption
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class PeraEventTrackerImplTest {
    private val mockFirebaseAnalytics: FirebaseAnalytics = mockk(relaxed = true)
    private val mockPeraExceptionLogger: PeraExceptionLogger = mockk(relaxed = true)
    private val mockGetReferrerData: GetReferrerData = mockk()
    private val mockIsStrongBoxUsedForEncryption: IsStrongBoxUsedForEncryption = mockk()
    private val testReferrerData = ReferrerData(
        utmSource = "test_source",
        utmMedium = "test_medium",
        utmCampaign = "test_campaign",
        utmTerm = "test_term",
        utmContent = "test_content"
    )

    private var sut: PeraEventTrackerImpl = PeraEventTrackerImpl(
        mockFirebaseAnalytics,
        mockPeraExceptionLogger,
        mockGetReferrerData,
        mockIsStrongBoxUsedForEncryption
    )

    @Test
    fun `EXPECT event logged with referral data WHEN logEvent is called with only event name`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns true

        val eventName = "test_event"

        sut.logEvent(eventName)

        coVerify(exactly = 1) { mockGetReferrerData.invoke() }
        coVerify(exactly = 1) { mockIsStrongBoxUsedForEncryption.invoke() }
        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }

    @Test
    fun `EXPECT event logged with merged data WHEN logEvent is called with event name and payload`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns true

        val eventName = "test_event_with_payload"
        val payload = mapOf(
            "string_param" to "string_value",
            "int_param" to 42,
            "boolean_param" to true
        )

        sut.logEvent(eventName, payload)

        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }

    @Test
    fun `EXPECT event logged successfully WHEN referral data has null fields`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns true

        val partialReferrerData = ReferrerData(
            utmSource = "partial_source",
            utmMedium = null,
            utmCampaign = "partial_campaign",
            utmTerm = null,
            utmContent = null
        )

        coEvery { mockGetReferrerData.invoke() } returns partialReferrerData

        val eventName = "test_event_partial_referral"

        sut.logEvent(eventName)

        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }

    @Test
    fun `EXPECT event logged with all data types WHEN payload contains various data types`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns true

        val eventName = "test_complex_payload"
        val complexPayload = mapOf(
            "string_param" to "string_value",
            "int_param" to 42,
            "boolean_param" to true,
            "long_param" to 9999L,
            "float_param" to 3.14f,
            "double_param" to 2.71828,
            "char_param" to 'A',
            "byte_param" to 127.toByte(),
            "short_param" to 32767.toShort()
        )

        sut.logEvent(eventName, complexPayload)

        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }

    @Test
    fun `EXPECT event logged with only referral data WHEN payload is empty`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns true

        val eventName = "test_empty_payload"
        val emptyPayload = emptyMap<String, Any>()

        sut.logEvent(eventName, emptyPayload)

        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }

    @Test
    fun `EXPECT true strongbox value added to bundle WHEN strongbox check returns true`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns true

        val eventName = "test_strongbox_true"

        sut.logEvent(eventName)

        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }

    @Test
    fun `EXPECT false strongbox value added to bundle WHEN strongbox check returns false`() = runTest {
        coEvery { mockGetReferrerData.invoke() } returns testReferrerData
        coEvery { mockIsStrongBoxUsedForEncryption.invoke() } returns false

        val eventName = "test_strongbox_false"

        sut.logEvent(eventName)

        verify(exactly = 1) { mockFirebaseAnalytics.logEvent(eq(eventName), any()) }
    }
}
