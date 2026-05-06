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

package com.algorand.android.ui.backup.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupAccounts
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupContacts
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewEvent
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewState
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewState.LatestSyncState
import com.algorand.android.usecase.ContactUseCase
import com.algorand.android.utils.MONTH_DAY_YEAR_TIME_PATTERN
import com.algorand.android.utils.format
import com.algorand.android.utils.toShortenedAddress
import com.algorand.android.utils.toZonedDateTimeFromMillis
import com.algorand.backup.domain.model.BackupSyncResult
import com.algorand.backup.domain.model.BackupSyncStatus
import com.algorand.backup.domain.model.LatestSync
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.DeleteBackup
import com.algorand.backup.domain.usecase.DisableBackup
import com.algorand.backup.domain.usecase.GetBackupId
import com.algorand.backup.domain.usecase.GetLatestSync
import com.algorand.android.ui.backup.list.usecase.GetBackupLocalAccounts
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class BackupOverviewViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    getBackupId: GetBackupId,
    private val getBackupLocalAccounts: GetBackupLocalAccounts,
    private val contactUseCase: ContactUseCase,
    private val getNotSyncedBackupAccounts: GetNotSyncedBackupAccounts,
    private val getNotSyncedBackupContacts: GetNotSyncedBackupContacts,
    private val getLatestSync: GetLatestSync,
    private val backupSyncManager: BackupSyncManager,
    private val disableBackup: DisableBackup,
    private val deleteBackup: DeleteBackup
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        val credentialAddress = getBackupId()?.address?.toShortenedAddress().orEmpty()
        stateDelegate.setDefaultState(ViewState(credentialAddress = credentialAddress))
        loadProtectedDataCounts()
        observeSyncStatus()
    }

    fun refresh() {
        loadProtectedDataCounts()
    }

    fun disableBackup() {
        viewModelScope.launch {
            disableBackup.invoke()
            eventDelegate.sendEvent(ViewEvent.NavigateBack)
        }
    }

    fun deleteBackup() {
        viewModelScope.launch {
            deleteBackup.invoke()
            eventDelegate.sendEvent(ViewEvent.NavigateBack)
        }
    }

    private fun loadProtectedDataCounts() {
        viewModelScope.launch {
            val localAccounts = getBackupLocalAccounts()
            val accountCount = localAccounts.count { it.isBackedUp }
            val contactCount = contactUseCase.getAllContacts().size
            val notSyncedAccountCount = getNotSyncedBackupAccounts().size
            val notSyncedContactCount = getNotSyncedBackupContacts().size
            val latestSync = getLatestSync()?.toUiModel()
            stateDelegate.updateState { current ->
                current.copy(
                    accountCount = accountCount,
                    contactCount = contactCount,
                    notSyncedAccountCount = notSyncedAccountCount,
                    notSyncedContactCount = notSyncedContactCount,
                    latestSync = latestSync
                )
            }
        }
    }

    private fun LatestSync.toUiModel(): LatestSyncState {
        return LatestSyncState(
            formattedTimestamp = timestampMillis.toZonedDateTimeFromMillis().format(MONTH_DAY_YEAR_TIME_PATTERN),
            isSuccess = result == BackupSyncResult.SUCCESS
        )
    }

    private fun observeSyncStatus() {
        backupSyncManager.syncStatus
            .onEach { status ->
                if (status !is BackupSyncStatus.Syncing) {
                    loadProtectedDataCounts()
                }
            }
            .launchIn(viewModelScope)
    }

    data class ViewState(
        val credentialAddress: String = "",
        val latestSync: LatestSyncState? = null,
        val accountCount: Int = 0,
        val contactCount: Int = 0,
        val notSyncedAccountCount: Int = 0,
        val notSyncedContactCount: Int = 0
    ) {
        data class LatestSyncState(val formattedTimestamp: String, val isSuccess: Boolean)
    }

    sealed interface ViewEvent {
        data object NavigateBack : ViewEvent
    }
}
