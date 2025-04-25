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

package com.algorand.android.modules.settings.data.repository

import com.algorand.wallet.foundation.cache.PersistentCache
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MigrationTo6xRepositoryImplTest {

    private var mockPersistentCache: PersistentCache<Boolean> = mockk()
    private var sut: MigrationTo6xRepositoryImpl = MigrationTo6xRepositoryImpl(mockPersistentCache)

    @Test
    fun `EXPECT false WHEN getMigratedTo6xCheck is called and cache returns null`() = runTest {
        coEvery { mockPersistentCache.get() } returns null

        val result = sut.getMigratedTo6xCheck()

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN getMigratedTo6xCheck is called and cache returns true`() = runTest {
        coEvery { mockPersistentCache.get() } returns true

        val result = sut.getMigratedTo6xCheck()

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN getMigratedTo6xCheck is called and cache returns false`() = runTest {
        coEvery { mockPersistentCache.get() } returns false

        val result = sut.getMigratedTo6xCheck()

        assertFalse(result)
    }
}
