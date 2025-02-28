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

package com.algorand.wallet.analytics.data.service

import com.algorand.wallet.analytics.PeraReferrerQueryParamParserImpl
import com.algorand.wallet.analytics.domain.model.ReferrerData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PeraReferrerQueryParamParserImplTest {

    private lateinit var sut: PeraReferrerQueryParamParserImpl

    @Before
    fun setup() {
        sut = PeraReferrerQueryParamParserImpl()
    }

    @Test
    fun `getReferrerData should parse all UTM parameters correctly`() {
        val queryString = "utm_source=pera_website&utm_medium=cpc" +
                "&utm_campaign=download_app&utm_term=crypto+wallet" +
                "&utm_content=textlink"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "cpc",
            utmCampaign = "download_app",
            utmTerm = "crypto wallet",
            utmContent = "textlink"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should handle partial UTM parameters`() {
        val queryString = "utm_source=pera_website&utm_medium=social"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "social",
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should handle URL encoded values`() {
        val queryString = "utm_source=pera_website&utm_content=download%20app%20%2325"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = "download app #25"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should return empty ReferrerData for null input`() {
        val queryString: String? = null

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should return empty ReferrerData for empty string`() {
        val queryString = ""

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should ignore non-UTM parameters`() {
        val queryString = "utm_source=pera_website&random_param=123&utm_medium=social&another_param=abc"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "social",
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should handle malformed query parameters`() {
        val queryString = "utm_source=pera_website&broken_param&utm_medium=cpc&=invalid"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "cpc",
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should properly process multiple parameters`() {
        val queryString = "param1=value1&utm_source=pera_website&param2=value2&utm_medium=organic&param3=value3"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "organic",
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should handle query string without ampersands`() {
        val queryString = "utm_source=pera_website"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should handle query parameter without value`() {
        val queryString = "utm_source=pera_website&broken_param"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getReferrerData should handle query parameter without key`() {
        val queryString = "utm_source=pera_website&=invalid"

        val result = sut.getReferrerData(queryString)

        val expected = ReferrerData(
            utmSource = "pera_website",
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )
        assertEquals(expected, result)
    }
}
