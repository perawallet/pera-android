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

package com.algorand.wallet.privacy.data.mapper

import com.algorand.wallet.privacy.data.model.PrivacyModeCacheValue
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultPrivacyModeCacheValueMapperTest {

    private val sut = DefaultPrivacyModeCacheValueMapper()

    @Test
    fun `EXPECT cache value enabled WHEN privacy mode is enabled`() {
        val result = sut(PrivacyMode.Enabled)

        assertEquals(PrivacyModeCacheValue.ENABLED, result)
    }

    @Test
    fun `EXPECT cache value disabled WHEN privacy mode is disabled`() {
        val result = sut(PrivacyMode.Disabled)

        assertEquals(PrivacyModeCacheValue.DISABLED, result)
    }
}
