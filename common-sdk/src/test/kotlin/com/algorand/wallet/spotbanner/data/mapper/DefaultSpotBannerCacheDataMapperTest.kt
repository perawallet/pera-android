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

package com.algorand.wallet.spotbanner.data.mapper

import com.algorand.wallet.spotbanner.data.model.SpotBannerCacheData
import com.algorand.wallet.spotbanner.data.model.SpotBannerResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultSpotBannerCacheDataMapperTest {

    private val sut = DefaultSpotBannerCacheDataMapper()

    @Test
    fun `EXPECT null WHEN id is null`() {
        val response = VALID_RESPONSE.copy(id = null)

        val result = sut.map(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN text is null`() {
        val response = VALID_RESPONSE.copy(text = null)

        val result = sut.map(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN optional fields are null`() {
        val response = VALID_RESPONSE.copy(isExternalButtonUrl = null)

        val result = sut.map(response)

        assertTrue(result?.isExternalButtonUrl ?: return)
    }

    @Test
    fun `EXPECT spot banner cache data WHEN all fields are valid`() {
        val result = sut.map(VALID_RESPONSE)

        assertEquals(SPOT_BANNER_CACHE_DATA, result)
    }

    private companion object {
        val VALID_RESPONSE = SpotBannerResponse(
            id = 1L,
            text = "Test Banner",
            image = "https://example.com/image.png",
            url = "https://example.com",
            isExternalButtonUrl = true
        )

        val SPOT_BANNER_CACHE_DATA = SpotBannerCacheData(
            id = 1L,
            text = "Test Banner",
            image = "https://example.com/image.png",
            url = "https://example.com",
            isExternalButtonUrl = true
        )
    }
}
