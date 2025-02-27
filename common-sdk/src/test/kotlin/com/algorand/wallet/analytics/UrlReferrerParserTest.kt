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

package com.algorand.wallet.analytics.domain.utils

import com.algorand.wallet.analytics.domain.usecases.model.ReferralData
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class UrlReferrerParserTest {

    private lateinit var sut: UrlReferrerParser

    @Before
    fun setUp() {
        sut = UrlReferrerParser()
        mockkStatic(URLDecoder::class)
    }

    @Test
    fun `getReferrerData should parse specific query string`() {
        val queryString = "utm_source=pera_website&utm_medium=referrer&utm_campaign=download_app&utm_term=keyword&utm_content=ad"

        every { URLDecoder.decode("pera_website", StandardCharsets.UTF_8.name()) } returns "pera_website"
        every { URLDecoder.decode("referrer", StandardCharsets.UTF_8.name()) } returns "referrer"
        every { URLDecoder.decode("download_app", StandardCharsets.UTF_8.name()) } returns "download_app"
        every { URLDecoder.decode("keyword", StandardCharsets.UTF_8.name()) } returns "keyword"
        every { URLDecoder.decode("ad", StandardCharsets.UTF_8.name()) } returns "ad"

        val result = sut.getReferrerData(queryString)

        assertEquals(
            ReferralData(
                utmSource = "pera_website",
                utmMedium = "referrer",
                utmCampaign = "download_app",
                utmTerm = "keyword",
                utmContent = "ad"
            ),
            result
        )
    }

    @Test
    fun `getReferrerData should handle empty query string`() {
        val queryString = ""
        val result = sut.getReferrerData(queryString)

        assertEquals(
            ReferralData(
                utmSource = null,
                utmMedium = null,
                utmCampaign = null,
                utmTerm = null,
                utmContent = null
            ),
            result
        )
    }

    @Test
    fun `getReferrerData should handle null query string`() {
        val queryString: String? = null
        val result = sut.getReferrerData(queryString)

        assertEquals(
            ReferralData(
                utmSource = null,
                utmMedium = null,
                utmCampaign = null,
                utmTerm = null,
                utmContent = null
            ),
            result
        )
    }

    @Test
    fun `getReferrerData should handle missing params`() {
        val queryString = "utm_source=pera_website&utm_medium=referrer"
        every { URLDecoder.decode("pera_website", StandardCharsets.UTF_8.name()) } returns "pera_website"
        every { URLDecoder.decode("referrer", StandardCharsets.UTF_8.name()) } returns "referrer"

        val result = sut.getReferrerData(queryString)

        assertEquals(
            ReferralData(
                utmSource = "pera_website",
                utmMedium = "referrer",
                utmCampaign = null,
                utmTerm = null,
                utmContent = null
            ),
            result
        )
    }

    @Test
    fun `getReferrerData should handle encoded characters`() {
        val queryString = "utm_source=pera%20website&utm_medium=cpc%20test"
        every { URLDecoder.decode("pera website", StandardCharsets.UTF_8.name()) } returns "pera website"
        every { URLDecoder.decode("cpc test", StandardCharsets.UTF_8.name()) } returns "cpc test"

        val result = sut.getReferrerData(queryString)

        assertEquals(
            ReferralData(
                utmSource = "pera website",
                utmMedium = "cpc test",
                utmCampaign = null,
                utmTerm = null,
                utmContent = null
            ),
            result
        )
    }

    @Test
    fun `getReferrerData should handle invalid params`() {
        val queryString = "invalid_param=value"
        val result = sut.getReferrerData(queryString)

        assertEquals(
            ReferralData(
                utmSource = null,
                utmMedium = null,
                utmCampaign = null,
                utmTerm = null,
                utmContent = null
            ),
            result
        )
    }
}
