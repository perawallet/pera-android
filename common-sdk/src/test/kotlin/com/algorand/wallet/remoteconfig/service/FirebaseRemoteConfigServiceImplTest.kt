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
import io.mockk.slot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.test.runTest

class FirebaseRemoteConfigServiceImplTest {

    private val remoteConfig: FirebaseRemoteConfig = mockk()
    private val fetchTask: Task<Boolean> = mockk()
    private val sut = FirebaseRemoteConfigServiceImpl(remoteConfig)

    @Test
    fun `EXPECT successful completion WHEN fetch succeeds`() = runTest {
        val listenerSlot = slot<OnCompleteListener<Boolean>>()
        every { remoteConfig.fetchAndActivate() } returns fetchTask
        every { fetchTask.addOnCompleteListener(capture(listenerSlot)) } answers {
            every { fetchTask.isSuccessful } returns true
            firstArg<OnCompleteListener<Boolean>>().onComplete(fetchTask)
            fetchTask
        }

        val result = sut.fetchRemoteConfig()

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT successful completion WHEN fetch fails`() = runTest {
        val listenerSlot = slot<OnCompleteListener<Boolean>>()
        every { remoteConfig.fetchAndActivate() } returns fetchTask
        every { fetchTask.addOnCompleteListener(capture(listenerSlot)) } answers {
            every { fetchTask.isSuccessful } returns false
            firstArg<OnCompleteListener<Boolean>>().onComplete(fetchTask)
            fetchTask
        }

        val result = sut.fetchRemoteConfig()

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT true WHEN getting enabled feature`() {
        val key = "enabled_feature"
        every { remoteConfig.getBoolean(key) } returns true

        val result = sut.getBoolean(key)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN getting disabled feature`() {
        val key = "disabled_feature"
        every { remoteConfig.getBoolean(key) } returns false

        val result = sut.getBoolean(key)

        assertFalse(result)
    }
}
