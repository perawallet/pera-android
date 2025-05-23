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
import javax.inject.Inject

internal class DefaultAccount6xMigrationManager @Inject constructor(
    private val saveMigratedTo6xCheck: SaveMigratedTo6xCheck,
    private val getMigratedTo6xCheck: GetMigratedTo6xCheck,
    private val migrateTo6x: MigrateTo6x,
    private val isSecretKeyValidatedForMigratedAccounts: IsSecretKeyValidatedForMigratedAccounts,
    private val setSecretKeyValidatedForMigratedAccounts: SetSecretKeyValidatedForMigratedAccounts,
    private val updateInvalidAlgo25AccountsToNoAuth: UpdateInvalidAlgo25AccountsToNoAuth
) : Account6xMigrationManager {

    override suspend fun migrateTo6xIfNeeded() {
        val isMigratedTo6X = getMigratedTo6xCheck()
        when {
            !isMigratedTo6X -> migrate()
            !isSecretKeyValidatedForMigratedAccounts() -> validateSecretKeys()
        }
    }

    private suspend fun migrate() {
        migrateTo6x()
        saveMigratedTo6xCheck(true)
    }

    private suspend fun validateSecretKeys() {
        updateInvalidAlgo25AccountsToNoAuth()
        setSecretKeyValidatedForMigratedAccounts()
    }
}
