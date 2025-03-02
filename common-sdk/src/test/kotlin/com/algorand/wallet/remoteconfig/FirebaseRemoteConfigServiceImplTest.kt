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

package com.algorand.wallet.remoteconfig.data.service

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.test.runTest

class FirebaseRemoteConfigServiceImplTest {

    private val remoteConfig: FirebaseRemoteConfig = mockk()
    private val sut = FirebaseRemoteConfigServiceImpl(remoteConfig)

    @Test
    fun given_fetchRemoteConfig_when_fetchAndActivate_then_success() = runTest {
        val task: Task<Boolean> = mockk()

        every { remoteConfig.fetchAndActivate() } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val argument = firstArg<OnCompleteListener<Boolean>>()
            argument.onComplete(task)
            task
        }
        every { task.isSuccessful } returns true

        sut.fetchRemoteConfig()
    }

    @Test
    fun given_fetchRemoteConfig_when_fetchAndActivate_then_failure() = runTest {
        val task: Task<Boolean> = mockk()

        every { remoteConfig.fetchAndActivate() } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val argument = firstArg<OnCompleteListener<Boolean>>()
            argument.onComplete(task)
            task
        }
        every { task.isSuccessful } returns false

        sut.fetchRemoteConfig()
    }

    @Test
    fun given_key_when_getBoolean_then_returnBoolean() {
        val key = "testKey"
        val expectedResult = true
        every { remoteConfig.getBoolean(key) } returns expectedResult

        val result = sut.getBoolean(key)

        assertEquals(expectedResult, result)
    }
}
