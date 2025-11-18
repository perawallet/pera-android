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

package com.algorand.android.migration.domain.manager

import com.algorand.android.migration.domain.usecase.GetMigratedTo6xCheck
import com.algorand.android.migration.domain.usecase.IsSecretKeyValidatedForMigratedAccounts
import com.algorand.android.migration.domain.usecase.MigrateTo6x
import com.algorand.android.migration.domain.usecase.SaveMigratedTo6xCheck
import com.algorand.android.migration.domain.usecase.SetSecretKeyValidatedForMigratedAccounts
import com.algorand.wallet.account.local.domain.usecase.UpdateInvalidAlgo25AccountsToNoAuth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultAccount6xMigrationManagerTest {

    private val saveMigratedTo6xCheck: SaveMigratedTo6xCheck = mockk(relaxed = true)
    private val getMigratedTo6xCheck: GetMigratedTo6xCheck = mockk(relaxed = true)
    private val migrateTo6x: MigrateTo6x = mockk(relaxed = true)
    private val isSecretKeyValidatedForMigratedAccounts: IsSecretKeyValidatedForMigratedAccounts = mockk(relaxed = true)
    private val setSecretKeyValidatedForMigratedAccounts: SetSecretKeyValidatedForMigratedAccounts = mockk(
        relaxed = true
    )
    private val updateInvalidAlgo25AccountsToNoAuth: UpdateInvalidAlgo25AccountsToNoAuth = mockk(relaxed = true)

    private val sut = DefaultAccount6xMigrationManager(
        saveMigratedTo6xCheck = saveMigratedTo6xCheck,
        getMigratedTo6xCheck = getMigratedTo6xCheck,
        migrateTo6x = migrateTo6x,
        isSecretKeyValidatedForMigratedAccounts = isSecretKeyValidatedForMigratedAccounts,
        setSecretKeyValidatedForMigratedAccounts = setSecretKeyValidatedForMigratedAccounts,
        updateInvalidAlgo25AccountsToNoAuth = updateInvalidAlgo25AccountsToNoAuth
    )

    @Test
    fun `EXPECT accounts to be migrated WHEN they are not migrated before`(): TestResult = runTest {
        coEvery { getMigratedTo6xCheck() } returns false

        sut.migrateTo6xIfNeeded()

        coVerify(exactly = 1) { migrateTo6x() }
        coVerify(exactly = 1) { saveMigratedTo6xCheck(true) }
        coVerify(exactly = 0) { updateInvalidAlgo25AccountsToNoAuth() }
        coVerify(exactly = 0) { setSecretKeyValidatedForMigratedAccounts() }
        coVerify(exactly = 0) { isSecretKeyValidatedForMigratedAccounts() }
    }

    @Test
    fun `EXPECT accounts to be migrated only once`(): TestResult = runTest {
        coEvery { getMigratedTo6xCheck() } returns true

        sut.migrateTo6xIfNeeded()

        coVerify(exactly = 0) { migrateTo6x() }
        coVerify(exactly = 0) { saveMigratedTo6xCheck(any()) }
    }

    @Test
    fun `EXPECT secret keys to be validated WHEN they are not validated before`(): TestResult = runTest {
        coEvery { getMigratedTo6xCheck() } returns true
        coEvery { isSecretKeyValidatedForMigratedAccounts() } returns false

        sut.migrateTo6xIfNeeded()

        coVerify(exactly = 0) { migrateTo6x() }
        coVerify(exactly = 0) { saveMigratedTo6xCheck(any()) }
        coVerify(exactly = 1) { updateInvalidAlgo25AccountsToNoAuth() }
        coVerify(exactly = 1) { setSecretKeyValidatedForMigratedAccounts() }
    }

    @Test
    fun `EXPECT secret keys to not be validated WHEN they are already validated before`(): TestResult = runTest {
        coEvery { getMigratedTo6xCheck() } returns true
        coEvery { isSecretKeyValidatedForMigratedAccounts() } returns true

        sut.migrateTo6xIfNeeded()

        coVerify(exactly = 0) { migrateTo6x() }
        coVerify(exactly = 0) { saveMigratedTo6xCheck(any()) }
        coVerify(exactly = 0) { updateInvalidAlgo25AccountsToNoAuth() }
        coVerify(exactly = 0) { setSecretKeyValidatedForMigratedAccounts() }
    }

    @Test
    fun `EXPECT account migration and then secret key validation`(): TestResult = runTest {
        coEvery { getMigratedTo6xCheck() } returnsMany listOf(false, true, true)
        coEvery { isSecretKeyValidatedForMigratedAccounts() } returnsMany listOf(false, true)

        sut.migrateTo6xIfNeeded()
        sut.migrateTo6xIfNeeded()
        sut.migrateTo6xIfNeeded()

        coVerify(exactly = 1) { migrateTo6x() }
        coVerify(exactly = 1) { saveMigratedTo6xCheck(true) }
        coVerify(exactly = 1) { updateInvalidAlgo25AccountsToNoAuth() }
        coVerify(exactly = 1) { setSecretKeyValidatedForMigratedAccounts() }
    }
}
