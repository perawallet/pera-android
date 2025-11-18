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

package com.algorand.wallet.privacy.data.repository

import com.algorand.test.test
import com.algorand.wallet.foundation.cache.FlowPersistentCache
import com.algorand.wallet.privacy.data.mapper.PrivacyModeCacheValueMapper
import com.algorand.wallet.privacy.data.mapper.PrivacyModeMapper
import com.algorand.wallet.privacy.data.model.PrivacyModeCacheValue
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultPrivacyModeRepositoryTest {

    private val flowPersistentCache: FlowPersistentCache<PrivacyModeCacheValue> = mockk(relaxed = true)
    private val privacyModeMapper: PrivacyModeMapper = mockk()
    private val privacyModeCacheValueMapper: PrivacyModeCacheValueMapper = mockk()

    private val sut = DefaultPrivacyModeRepository(flowPersistentCache, privacyModeMapper, privacyModeCacheValueMapper)

    @Test
    fun `EXPECT cached privacy mode WHEN get privacy mode is invoked`(): TestResult = runTest {
        every { flowPersistentCache.get() } returns PrivacyModeCacheValue.ENABLED
        every { privacyModeMapper(PrivacyModeCacheValue.ENABLED) } returns PrivacyMode.Enabled

        val result = sut.getPrivacyMode()

        assertEquals(PrivacyMode.Enabled, result)
    }

    @Test
    fun `EXPECT privacy mode to be cached WHEN set privacy mode is invoked`(): TestResult = runTest {
        every { privacyModeCacheValueMapper(PrivacyMode.Disabled) } returns PrivacyModeCacheValue.DISABLED

        sut.setPrivacyMode(PrivacyMode.Disabled)

        verify { flowPersistentCache.put(PrivacyModeCacheValue.DISABLED) }
    }

    @Test
    fun `EXPECT cache flow updates WHEN privacy mode changes`() {
        val cacheFlow = MutableStateFlow(PrivacyModeCacheValue.ENABLED)
        every { flowPersistentCache.observe() } returns cacheFlow
        every { privacyModeMapper(PrivacyModeCacheValue.ENABLED) } returns PrivacyMode.Enabled
        every { privacyModeMapper(PrivacyModeCacheValue.DISABLED) } returns PrivacyMode.Disabled
        every { privacyModeCacheValueMapper(PrivacyMode.Enabled) } returns PrivacyModeCacheValue.ENABLED
        every { privacyModeCacheValueMapper(PrivacyMode.Disabled) } returns PrivacyModeCacheValue.DISABLED

        val testObserver = sut.getPrivacyModeFlow().test()
        cacheFlow.value = PrivacyModeCacheValue.DISABLED
        cacheFlow.value = PrivacyModeCacheValue.ENABLED

        testObserver.assertValueHistory(PrivacyMode.Enabled, PrivacyMode.Disabled, PrivacyMode.Enabled)
    }
}
