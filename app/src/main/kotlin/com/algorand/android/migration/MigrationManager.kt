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

import androidx.lifecycle.Lifecycle
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.manager.LifecycleAwareManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Singleton
class MigrationManager @Inject constructor(
    private val lifecycleAwareManager: LifecycleAwareManager,
    private val accountMigrationManager: AccountMigrationManager,
    private val encryptedPinMigrationManager: EncryptedPinMigrationManager,
    private val account6xMigrationManager: Account6xMigrationManager,
    private val peraExceptionLogger: PeraExceptionLogger
) : LifecycleAwareManager.LifecycleAwareManagerListener {

    private val _migrationResultFlow = MutableSharedFlow<PeraResult<Boolean>>(replay = 1)
    private val migrationResultFlow = _migrationResultFlow.asSharedFlow()

    fun initialize(lifecycle: Lifecycle) {
        lifecycleAwareManager.setListener(this)
        lifecycle.addObserver(lifecycleAwareManager)
    }

    override suspend fun onInitializeManager(coroutineScope: CoroutineScope) {
        lifecycleAwareManager.startJob()
    }

    override suspend fun onStartJob(coroutineScope: CoroutineScope) {
        runManagerJob(coroutineScope)
    }

    fun getMigrationResultFlow() = migrationResultFlow

    private fun runManagerJob(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                encryptedPinMigrationManager.makeMigrationIfNeeded()
                accountMigrationManager.makeMigrationIfNeeded()
                account6xMigrationManager.migrateTo6xIfNeeded()
                _migrationResultFlow.emit(PeraResult.Success(true))
            } catch (e: Exception) {
                peraExceptionLogger.logException(e)
                _migrationResultFlow.emit(PeraResult.Error(e))
            }
        }
    }
}
