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

package com.algorand.android.migration

import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class MigrationManager @Inject constructor(
    private val accountMigrationManager: AccountMigrationManager,
    private val encryptedPinMigrationManager: EncryptedPinMigrationManager,
    private val account6xMigrationManager: Account6xMigrationManager,
    private val peraExceptionLogger: PeraExceptionLogger
) {
    fun makeMigrationsAsFlow(): Flow<PeraResult<Boolean>> = flow {
        try {
            encryptedPinMigrationManager.makeMigrationIfNeeded()
            accountMigrationManager.makeMigrationIfNeeded()
            account6xMigrationManager.migrateTo6xIfNeeded()
            emit(PeraResult.Success(true))
        } catch (e: Exception) {
            peraExceptionLogger.logException(e)
            emit(PeraResult.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
