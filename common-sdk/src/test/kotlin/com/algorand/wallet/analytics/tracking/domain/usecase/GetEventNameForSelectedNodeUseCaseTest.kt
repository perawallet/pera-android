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

package com.algorand.wallet.analytics.tracking.domain.usecase

import com.algorand.wallet.node.domain.usecase.IsSelectedNodeTestnet
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetEventNameForSelectedNodeUseCaseTest {

    private val isSelectedNodeTestnet: IsSelectedNodeTestnet = mockk()

    private val sut = GetEventNameForSelectedNodeUseCase(isSelectedNodeTestnet)

    @Test
    fun `EXPECT t_ prefix WHEN selected node is testnet`(): TestResult = runTest {
        coEvery { isSelectedNodeTestnet() } returns true

        val result = sut("some_event_name")

        val expected = "t_some_event_name"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT the same event name WHEN selected node is not testnet`(): TestResult = runTest {
        coEvery { isSelectedNodeTestnet() } returns false

        val result = sut("some_event_name")

        val expected = "some_event_name"
        assertEquals(expected, result)
    }
}
