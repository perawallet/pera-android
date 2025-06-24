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
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSpotBannerMapperTest {

    private val sut = DefaultSpotBannerMapper()

    @Test
    fun `EXPECT mapped spot banner`() {
        val result = sut.map(SPOT_BANNER_CACHE_DATA)

        assertEquals(SPOT_BANNER, result)
    }

    private companion object {
        val SPOT_BANNER_CACHE_DATA = SpotBannerCacheData(
            id = 1L,
            text = "Sample Banner",
            image = "https://example.com/image.png",
            url = "https://example.com",
            isExternalButtonUrl = true
        )

        val SPOT_BANNER = SpotBanner.Generic(
            id = 1L,
            text = "Sample Banner",
            image = "https://example.com/image.png",
            url = "https://example.com",
            isExternalButtonUrl = true
        )
    }
}
