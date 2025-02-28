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

package com.algorand.wallet.analytics.repository

import android.content.SharedPreferences
import com.algorand.wallet.analytics.data.repository.ReferrerRepositoryImpl
import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CAMPAIGN
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CONTENT
import com.algorand.wallet.analytics.domain.util.GA4.UTM_MEDIUM
import com.algorand.wallet.analytics.domain.util.GA4.UTM_SOURCE
import com.algorand.wallet.analytics.domain.util.GA4.UTM_TERM
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking

class ReferrerRepositoryImplTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var sut: ReferrerRepositoryImpl

    @Before
    fun setUp() {
        sharedPreferences = mockk()
        sharedPreferencesEditor = mockk()
        sut = ReferrerRepositoryImpl(sharedPreferences)

        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } returns Unit
        every { sharedPreferences.getString(any(), any()) } returns null
    }

    @Test
    fun `EXPECT data saved to SharedPreferences WHEN saveReferrerData is called with valid data`() = runBlocking {
        val referrerData = ReferrerData(
            utmSource = "source",
            utmMedium = "medium",
            utmCampaign = "campaign",
            utmTerm = "term",
            utmContent = "content"
        )

        sut.saveReferrerData(referrerData)

        verify { sharedPreferencesEditor.putString(UTM_SOURCE, "source") }
        verify { sharedPreferencesEditor.putString(UTM_MEDIUM, "medium") }
        verify { sharedPreferencesEditor.putString(UTM_CAMPAIGN, "campaign") }
        verify { sharedPreferencesEditor.putString(UTM_TERM, "term") }
        verify { sharedPreferencesEditor.putString(UTM_CONTENT, "content") }
        verify { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `EXPECT no values saved WHEN saveReferrerData is called with all null values`() = runBlocking {
        val referrerData = ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )

        sut.saveReferrerData(referrerData)

        verify(exactly = 0) { sharedPreferencesEditor.putString(any(), any()) }
        verify { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `EXPECT complete ReferrerData WHEN getReferrerData is called and SharedPreferences has all values`() = runBlocking {
        every { sharedPreferences.getString(UTM_SOURCE, null) } returns "source"
        every { sharedPreferences.getString(UTM_MEDIUM, null) } returns "medium"
        every { sharedPreferences.getString(UTM_CAMPAIGN, null) } returns "campaign"
        every { sharedPreferences.getString(UTM_TERM, null) } returns "term"
        every { sharedPreferences.getString(UTM_CONTENT, null) } returns "content"

        val result = sut.getReferrerData()

        assertEquals(ReferrerData(
            utmSource = "source",
            utmMedium = "medium",
            utmCampaign = "campaign",
            utmTerm = "term",
            utmContent = "content"
        ), result)
    }

    @Test
    fun `EXPECT ReferrerData with null values WHEN getReferrerData is called and SharedPreferences has no values`() = runBlocking {
        val result = sut.getReferrerData()

        assertEquals(ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        ), result)
    }
}
