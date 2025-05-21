/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.migration.data.repository

import com.algorand.wallet.foundation.cache.PersistentCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MigrationTo6xRepositoryImplTest {

    private var migrationTo6xCache: PersistentCache<Boolean> = mockk()
    private var secretKeyValidationCache: PersistentCache<Boolean> = mockk(relaxed = true)
    private var sut = MigrationTo6xRepositoryImpl(migrationTo6xCache, secretKeyValidationCache)

    @Test
    fun `EXPECT false WHEN getMigratedTo6xCheck is called and cache returns null`() = runTest {
        every { migrationTo6xCache.get() } returns null

        val result = sut.getMigratedTo6xCheck()

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN getMigratedTo6xCheck is called and cache returns true`() = runTest {
        every { migrationTo6xCache.get() } returns true

        val result = sut.getMigratedTo6xCheck()

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN getMigratedTo6xCheck is called and cache returns false`() = runTest {
        every { migrationTo6xCache.get() } returns false

        val result = sut.getMigratedTo6xCheck()

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN isSecretKeyValidatedForMigratedAccounts is called and cache returns null`() = runTest {
        every { secretKeyValidationCache.get() } returns null

        val result = sut.isSecretKeyValidatedForMigratedAccounts()

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN isSecretKeyValidatedForMigratedAccounts is called and cache returns true`() = runTest {
        every { secretKeyValidationCache.get() } returns true

        val result = sut.isSecretKeyValidatedForMigratedAccounts()

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN isSecretKeyValidatedForMigratedAccounts is called and cache returns false`() = runTest {
        every { secretKeyValidationCache.get() } returns false

        val result = sut.isSecretKeyValidatedForMigratedAccounts()

        assertFalse(result)
    }

    @Test
    fun `EXPECT secret key validation cache to be set to true`() = runTest {
        sut.setSecretKeyValidatedForMigratedAccounts()

        verify { secretKeyValidationCache.put(true) }
    }
}
