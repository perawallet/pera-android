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

package com.algorand.wallet.analytics.data

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InstallReferrerApiClientTest {
    private val mockContext = mockk<Context>()
    private val mockReferrerClient = mockk<InstallReferrerClient>()
    private val mockBuilder = mockk<InstallReferrerClient.Builder>()

    private lateinit var sut: InstallReferrerApiClient

    @Before
    fun setup() {
        sut = InstallReferrerApiClient(mockContext)

        mockkStatic(InstallReferrerClient::class)

        every { InstallReferrerClient.newBuilder(mockContext) } returns mockBuilder
        every { mockBuilder.build() } returns mockReferrerClient
    }

    @Test
    fun `initialize should create and return a new InstallReferrerClient`() {
        val result = sut.initialize()

        verify(exactly = 1) { InstallReferrerClient.newBuilder(mockContext) }
        verify(exactly = 1) { mockBuilder.build() }
        assertEquals(mockReferrerClient, result)
    }
}