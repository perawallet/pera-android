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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Singleton
class MigrationManager @Inject constructor(
    private val accountMigrationManager: AccountMigrationManager,
    private val encryptedPinMigrationManager: EncryptedPinMigrationManager,
    private val account6xMigrationManager: Account6xMigrationManager,
    private val peraExceptionLogger: PeraExceptionLogger
) : DefaultLifecycleObserver {

    private val _migrationResultFlow = MutableSharedFlow<PeraResult<Unit>>()
    val migrationResultFlow: SharedFlow<PeraResult<Unit>> = _migrationResultFlow.asSharedFlow()

    private var coroutineScope: CoroutineScope? = null

    fun initialize(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        runMigration()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun runMigration() {
        coroutineScope?.launch(Dispatchers.IO) {
            try {
                encryptedPinMigrationManager.makeMigrationIfNeeded()
                accountMigrationManager.makeMigrationIfNeeded()
                account6xMigrationManager.migrateTo6xIfNeeded()
                _migrationResultFlow.emit(PeraResult.Success(Unit))
            } catch (e: Exception) {
                peraExceptionLogger.logException(e)
                _migrationResultFlow.emit(PeraResult.Error(e))
            }
        }
    }
}
