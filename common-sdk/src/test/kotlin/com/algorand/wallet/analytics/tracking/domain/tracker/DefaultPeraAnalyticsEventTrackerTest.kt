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

package com.algorand.wallet.analytics.tracking.domain.tracker

import com.algorand.wallet.analytics.tracking.domain.repository.PeraAnalyticsRepository
import com.algorand.wallet.analytics.tracking.domain.usecase.GetEventNameForSelectedNode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultPeraAnalyticsEventTrackerTest {

    private val peraAnalyticsRepository: PeraAnalyticsRepository = mockk(relaxed = true)
    private val getEventNameForSelectedNode: GetEventNameForSelectedNode = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var sut: DefaultPeraAnalyticsEventTracker

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sut = DefaultPeraAnalyticsEventTracker(
            peraAnalyticsRepository,
            getEventNameForSelectedNode,
            testScope
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cancel()
    }

    @Test
    fun `EXPECT event to be logged with normalized name`() {
        coEvery { getEventNameForSelectedNode(EVENT_NAME) } returns "t_$EVENT_NAME"

        sut.logEvent(EVENT_NAME)

        coVerify { peraAnalyticsRepository.logEvent("t_$EVENT_NAME") }
    }

    @Test
    fun `EXPECT event to be logged with normalized name and a payload`() {
        val payloadMap = mapOf("key" to "value")
        coEvery { getEventNameForSelectedNode(EVENT_NAME) } returns EVENT_NAME

        sut.logEvent(EVENT_NAME, payloadMap)

        coVerify { peraAnalyticsRepository.logEvent(EVENT_NAME, payloadMap) }
    }

    private companion object {
        const val EVENT_NAME = "some_event_name"
    }
}
