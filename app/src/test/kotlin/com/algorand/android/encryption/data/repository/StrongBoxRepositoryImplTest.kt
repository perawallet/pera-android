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

package com.algorand.android.encryption.data.repository

import com.algorand.wallet.foundation.cache.PersistentCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class StrongBoxRepositoryImplTest {

    private var strongBoxUsedStorage: PersistentCache<Boolean> = mockk(relaxed = true)
    private var sut: StrongBoxRepositoryImpl = StrongBoxRepositoryImpl(strongBoxUsedStorage)

    @Test
    fun `EXPECT value saved to storage WHEN saveStrongBoxUsed is called`() = runTest {
        val check = true

        sut.saveStrongBoxUsed(check)

        coVerify(exactly = 1) { strongBoxUsedStorage.put(check) }
    }

    @Test
    fun `EXPECT storage value returned WHEN getStrongBoxUsed is called`() = runTest {
        val expectedValue = true
        coEvery { strongBoxUsedStorage.get() } returns expectedValue

        val result = sut.getStrongBoxUsed()

        assertEquals(expectedValue, result)
    }

    @Test
    fun `EXPECT false returned WHEN getStrongBoxUsed is called and storage returns null`() = runTest {
        coEvery { strongBoxUsedStorage.get() } returns null

        val result = sut.getStrongBoxUsed()

        assertFalse(result)
    }
}
