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

package com.algorand.wallet.banner.data.mapper

import com.algorand.wallet.banner.data.model.BannerDetailResponse
import com.algorand.wallet.banner.data.model.BannerTypeResponse
import com.algorand.wallet.banner.domain.model.Banner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultBannerMapperTest {

    private val sut = DefaultBannerMapper()

    @Test
    fun `EXPECT null WHEN banner id is null`() {
        val response = BANNER_RESPONSE.copy(bannerId = null)

        val result = sut.map(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT governance banner type WHEN banner type is GOVERNANCE`() {
        val response = BANNER_RESPONSE.copy(bannerTypeResponse = BannerTypeResponse.GOVERNANCE)

        val result = sut.map(response)

        val expected = BANNER.copy(type = Banner.BannerType.Governance)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT staking banner type WHEN banner type is STAKING`() {
        val response = BANNER_RESPONSE.copy(bannerTypeResponse = BannerTypeResponse.STAKING)

        val result = sut.map(response)

        val expected = BANNER.copy(type = Banner.BannerType.Staking)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT card banner type WHEN banner type is CARD`() {
        val response = BANNER_RESPONSE.copy(bannerTypeResponse = BannerTypeResponse.CARD)

        val result = sut.map(response)

        val expected = BANNER.copy(type = Banner.BannerType.Card)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT generic banner type WHEN banner type is GENERIC`() {
        val response = BANNER_RESPONSE.copy(bannerTypeResponse = BannerTypeResponse.GENERIC)

        val result = sut.map(response)

        val expected = BANNER.copy(type = Banner.BannerType.Generic)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT generic banner type WHEN banner type is OTHER`() {
        val response = BANNER_RESPONSE.copy(bannerTypeResponse = BannerTypeResponse.OTHER)

        val result = sut.map(response)

        val expected = BANNER.copy(type = Banner.BannerType.Generic)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT generic banner type WHEN banner type is null`() {
        val response = BANNER_RESPONSE.copy(bannerTypeResponse = null)

        val result = sut.map(response)

        val expected = BANNER.copy(type = Banner.BannerType.Generic)
        assertEquals(expected, result)
    }

    private companion object {
        val BANNER_RESPONSE = BannerDetailResponse(
            bannerId = 1L,
            bannerTypeResponse = BannerTypeResponse.GENERIC,
            title = "Test Title",
            description = "Test Description",
            buttonText = "Test Button",
            buttonUrl = "https://example.com"
        )

        val BANNER = Banner(
            bannerId = 1L,
            title = "Test Title",
            description = "Test Description",
            buttonTitle = "Test Button",
            buttonUrl = "https://example.com",
            type = Banner.BannerType.Generic
        )
    }
}
