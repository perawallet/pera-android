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

package com.algorand.backup.domain.usecase

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.algorand.backup.domain.model.BackupItemChange
import com.algorand.backup.domain.model.BackupSyncStatus
import com.algorand.backup.domain.model.BackupWebSocketEvent
import com.algorand.backup.domain.model.SyncBackupResult
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class BackupSyncManager internal constructor(
    private val syncBackup: SyncBackup,
    private val pullAndImportSync: PullAndImportSync,
    private val hasBackup: HasBackup,
    private val connectBackupWebSocket: ConnectBackupWebSocket,
    private val disconnectBackupWebSocket: DisconnectBackupWebSocket,
    private val getBackupWebSocketEvents: GetBackupWebSocketEvents,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val itemObservers: Set<BackupItemObserver>,
    private val scope: CoroutineScope,
    private val onBackupDestroyed: suspend () -> Unit
) : DefaultLifecycleObserver {

    private val _syncStatus = MutableStateFlow<BackupSyncStatus>(BackupSyncStatus.Idle)
    val syncStatus: StateFlow<BackupSyncStatus> get() = _syncStatus

    private val syncMutex = Mutex()

    private var periodicJob: Job? = null
    private var webSocketJob: Job? = null
    private var observeJob: Job? = null
    private var debouncedSyncJob: Job? = null
    private var debouncedPullJob: Job? = null
    private var destroyJob: Job? = null

    override fun onResume(owner: LifecycleOwner) {
        if (!isBackupFeatureEnabled()) return
        startObservingChanges()
        if (!hasBackup()) return
        syncNow()
        startPeriodicSync()
        connectWebSocket()
    }

    override fun onPause(owner: LifecycleOwner) {
        stopPeriodicSync()
        stopObservingChanges()
        disconnectWebSocket()
    }

    fun enableSync() {
        if (!isBackupFeatureEnabled()) return
        destroyJob?.cancel()
        destroyJob = null
        _syncStatus.value = BackupSyncStatus.Idle
        syncNow()
        startPeriodicSync()
        connectWebSocket()
    }

    fun syncNow() {
        if (!hasBackup()) return
        scope.launch { runFullSync() }
    }

    fun stop() {
        stopPeriodicSync()
        stopObservingChanges()
        disconnectWebSocket()
        debouncedSyncJob?.cancel()
        debouncedPullJob?.cancel()
        if (_syncStatus.value != BackupSyncStatus.BackupDestroyed) {
            _syncStatus.value = BackupSyncStatus.Idle
        }
    }

    private fun startObservingChanges() {
        stopObservingChanges()
        val flows = itemObservers.map { observer -> observer.observeChanges(scope) }
        observeJob = flows
            .merge()
            .filter { it is BackupItemChange.SyncRequired }
            .onEach { scheduleDebouncedSync() }
            .launchIn(scope)
    }

    private fun stopObservingChanges() {
        observeJob?.cancel()
        observeJob = null
        itemObservers.forEach { it.reset() }
    }

    private fun scheduleDebouncedSync() {
        debouncedSyncJob?.cancel()
        debouncedSyncJob = scope.launch {
            delay(SYNC_DEBOUNCE_MS)
            runFullSync()
        }
    }

    private suspend fun runFullSync() {
        syncMutex.withLock {
            stopObservingChanges()
            _syncStatus.value = BackupSyncStatus.Syncing

            val result = syncBackup()

            if (result is SyncBackupResult.BackupDestroyed) {
                handleBackupDestroyed()
                return@withLock
            }

            _syncStatus.value = when (result) {
                is SyncBackupResult.Success -> BackupSyncStatus.UpToDate
                is SyncBackupResult.SuccessWithPendingChanges -> BackupSyncStatus.HasLocalChanges
                is SyncBackupResult.AlreadyRunning -> _syncStatus.value
                is SyncBackupResult.BackupDestroyed -> error("unreachable")
                is SyncBackupResult.Error -> BackupSyncStatus.Error(result.exception)
            }

            startObservingChanges()
        }
    }

    private fun connectWebSocket() {
        webSocketJob?.cancel()
        webSocketJob = getBackupWebSocketEvents()
            .onEach { event -> handleWebSocketEvent(event) }
            .launchIn(scope)
        connectBackupWebSocket(scope)
    }

    private fun disconnectWebSocket() {
        disconnectBackupWebSocket()
        webSocketJob?.cancel()
        webSocketJob = null
    }

    private fun handleWebSocketEvent(event: BackupWebSocketEvent) {
        when (event) {
            is BackupWebSocketEvent.ItemsUpdated -> scheduleDebouncedPull()
            is BackupWebSocketEvent.BackupDeleted -> {
                destroyJob = scope.launch { handleBackupDestroyed() }
            }
            is BackupWebSocketEvent.Connected,
            is BackupWebSocketEvent.Disconnected,
            is BackupWebSocketEvent.Error,
            is BackupWebSocketEvent.Unknown -> Unit
        }
    }

    private fun scheduleDebouncedPull() {
        if (!hasBackup()) return
        debouncedPullJob?.cancel()
        debouncedPullJob = scope.launch {
            delay(PULL_DEBOUNCE_MS)
            runPull()
        }
    }

    private suspend fun runPull() {
        syncMutex.withLock {
            stopObservingChanges()
            _syncStatus.value = BackupSyncStatus.Syncing

            val result = pullAndImportSync()

            if (result is SyncBackupResult.BackupDestroyed) {
                handleBackupDestroyed()
                return@withLock
            }

            val newStatus = when (result) {
                is SyncBackupResult.Success -> BackupSyncStatus.UpToDate
                is SyncBackupResult.Error -> BackupSyncStatus.Error(result.exception)
                else -> _syncStatus.value
            }
            _syncStatus.value = newStatus

            startObservingChanges()
        }
    }

    private suspend fun handleBackupDestroyed() {
        if (_syncStatus.value == BackupSyncStatus.BackupDestroyed) return
        _syncStatus.value = BackupSyncStatus.BackupDestroyed
        try {
            onBackupDestroyed()
        } catch (_: Exception) {
            stop()
        }
    }

    private fun startPeriodicSync() {
        stopPeriodicSync()
        periodicJob = scope.launch {
            while (true) {
                delay(SYNC_INTERVAL_MS)
                if (hasBackup()) {
                    runFullSync()
                }
            }
        }
    }

    private fun stopPeriodicSync() {
        periodicJob?.cancel()
        periodicJob = null
    }

    private fun isBackupFeatureEnabled(): Boolean = isFeatureToggleEnabled(FeatureToggle.BACKUP.key)

    private companion object {
        const val SYNC_DEBOUNCE_MS = 1000L
        const val PULL_DEBOUNCE_MS = 500L
        const val SYNC_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
    }
}
