/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.analytics.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

class FirebaseAnalyticsRepositoryImplTest {

    private lateinit var sut: FirebaseAnalyticsRepositoryImpl
    private val mockFirebaseInstallations: FirebaseInstallations = mockk()
    private val mockTask: Task<String> = mockk()

    @Before
    fun setup() {
        sut = FirebaseAnalyticsRepositoryImpl(mockFirebaseInstallations)
    }

    @Test
    fun `GIVEN successful task WHEN getFirebaseInstanceId called THEN returns installation id`() = runTest {
        val expectedId = "test-firebase-id-123"
        every { mockFirebaseInstallations.getId() } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns expectedId

        every { mockTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<com.google.android.gms.tasks.OnCompleteListener<String>>()
            listener.onComplete(mockTask)
            mockTask
        }

        val result = sut.getFirebaseInstanceId()

        assertEquals(expectedId, result)
    }

    @Test(expected = Exception::class)
    fun `GIVEN failed task WHEN getFirebaseInstanceId called THEN throws exception`() = runTest {
        val expectedException = Exception("Task failed")
        every { mockFirebaseInstallations.getId() } returns mockTask
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns expectedException
        every { mockTask.addOnCompleteListener(any()) } answers {
            firstArg<(Task<String>) -> Unit>().invoke(mockTask)
            mockTask
        }

        sut.getFirebaseInstanceId()
    }

    @Test(expected = Exception::class)
    fun `GIVEN failed task with null exception WHEN getFirebaseInstanceId called THEN throws default exception`() = runTest {
        every { mockFirebaseInstallations.getId() } returns mockTask
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns null
        every { mockTask.addOnCompleteListener(any()) } answers {
            firstArg<(Task<String>) -> Unit>().invoke(mockTask)
            mockTask
        }

        sut.getFirebaseInstanceId()
    }
}
