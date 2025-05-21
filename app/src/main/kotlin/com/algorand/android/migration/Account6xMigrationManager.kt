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

package com.algorand.android.migration

import com.algorand.android.migration.domain.usecase.GetMigratedTo6xCheck
import com.algorand.android.migration.domain.usecase.MigrateTo6x
import com.algorand.android.migration.domain.usecase.SaveMigratedTo6xCheck
import javax.inject.Inject

class Account6xMigrationManager @Inject constructor(
    private val saveMigratedTo6xCheck: SaveMigratedTo6xCheck,
    private val getMigratedTo6xCheck: GetMigratedTo6xCheck,
    private val migrateTo6x: MigrateTo6x
) {

    suspend fun migrateTo6xIfNeeded() {
        val isMigratedTo6X = getMigratedTo6xCheck()
        if (!isMigratedTo6X) {
            migrateTo6x()
            saveMigratedTo6xCheck.invoke(true)
        }
    }
}
