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

package com.algorand.wallet.analytics.domain.usecase

import com.algorand.wallet.analytics.domain.repository.FirebaseAnalyticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

class GetFirebaseInstanceIdUseCaseTest {

    private lateinit var sut: GetFirebaseInstanceIdUseCase
    private val mockRepository: FirebaseAnalyticsRepository = mockk()

    @Before
    fun setup() {
        sut = GetFirebaseInstanceIdUseCase(mockRepository)
    }

    @Test
    fun `GIVEN repository returns id WHEN usecase invoked THEN returns same id`() = runTest {
        val expectedId = "test-firebase-id-123"
        coEvery { mockRepository.getFirebaseInstanceId() } returns expectedId

        val result = sut.invoke()

        assertEquals(expectedId, result)
    }

    @Test(expected = Exception::class)
    fun `GIVEN repository throws exception WHEN usecase invoked THEN exception is propagated`() = runTest {
        val expectedException = Exception("Repository error")
        coEvery { mockRepository.getFirebaseInstanceId() } throws expectedException

        sut.invoke()
    }
}
