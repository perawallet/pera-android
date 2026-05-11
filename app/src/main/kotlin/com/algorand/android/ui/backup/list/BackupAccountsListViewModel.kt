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

package com.algorand.android.ui.backup.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.backup.list.BackupAccountsListViewModel.ViewEvent
import com.algorand.android.ui.backup.list.BackupAccountsListViewModel.ViewState
import com.algorand.android.ui.backup.list.model.BackupLocalAccountItem
import com.algorand.android.ui.backup.list.usecase.GetBackupLocalAccounts
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupAccounts
import com.algorand.backup.domain.model.BackupSyncStatus
import com.algorand.backup.domain.usecase.AddAccountToBackup
import com.algorand.backup.domain.usecase.BackupSyncManager
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
class BackupAccountsListViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getBackupLocalAccounts: GetBackupLocalAccounts,
    private val getNotSyncedBackupAccounts: GetNotSyncedBackupAccounts,
    private val addAccountToBackup: AddAccountToBackup,
    private val backupSyncManager: BackupSyncManager
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState())
        loadAccounts()
        observeSyncStatus()
    }

    fun refresh() {
        loadAccounts()
    }

    fun backUpAccount(address: String) {
        viewModelScope.launch {
            addAccountToBackup(address)
            backupSyncManager.syncNow()
            eventDelegate.sendEvent(ViewEvent.BackUpSuccess)
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            val localAccounts = getBackupLocalAccounts()
            val availableFromBackup = getNotSyncedBackupAccounts()
            stateDelegate.updateState {
                it.copy(
                    localAccounts = localAccounts,
                    notBackedUpCount = localAccounts.count { account -> !account.isBackedUp },
                    availableFromBackupCount = availableFromBackup.size
                )
            }
        }
    }

    private fun observeSyncStatus() {
        backupSyncManager.syncStatus
            .onEach { status ->
                if (status !is BackupSyncStatus.Syncing) {
                    loadAccounts()
                }
            }
            .launchIn(viewModelScope)
    }

    data class ViewState(
        val localAccounts: List<BackupLocalAccountItem> = emptyList(),
        val notBackedUpCount: Int = 0,
        val availableFromBackupCount: Int = 0
    ) {
        val hasAccountsToReview: Boolean
            get() = notBackedUpCount > 0 || availableFromBackupCount > 0
    }

    sealed interface ViewEvent {
        data object BackUpSuccess : ViewEvent
    }
}
