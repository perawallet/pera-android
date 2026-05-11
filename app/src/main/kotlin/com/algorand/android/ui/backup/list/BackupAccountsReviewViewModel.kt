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
import com.algorand.android.ui.backup.list.BackupAccountsReviewViewModel.ViewEvent
import com.algorand.android.ui.backup.list.BackupAccountsReviewViewModel.ViewState
import com.algorand.android.ui.backup.list.model.BackupAccountListItem
import com.algorand.android.ui.backup.list.model.BackupLocalAccountItem
import com.algorand.android.ui.backup.list.usecase.AddBackupAccountToLocal
import com.algorand.android.ui.backup.list.usecase.GetBackupLocalAccounts
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupAccounts
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.domain.model.BackupSyncStatus
import com.algorand.backup.domain.usecase.AddAccountToBackup
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.DeleteAccountFromBackup
import com.algorand.wallet.foundation.PeraResult
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
class BackupAccountsReviewViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getBackupLocalAccounts: GetBackupLocalAccounts,
    private val getNotSyncedBackupAccounts: GetNotSyncedBackupAccounts,
    private val addBackupAccountToLocal: AddBackupAccountToLocal,
    private val deleteAccountFromBackup: DeleteAccountFromBackup,
    private val addAccountToBackup: AddAccountToBackup,
    private val backupSyncManager: BackupSyncManager
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private var initialExpansionApplied = false
    private var pendingRemovalPayload: AddressBackupPayload? = null

    init {
        stateDelegate.setDefaultState(ViewState())
        viewModelScope.launch { reloadState() }
        observeSyncStatus()
    }

    fun refresh() {
        viewModelScope.launch { reloadState() }
    }

    fun toggleAddFromBackupExpanded() {
        stateDelegate.updateState { it.copy(isAddFromBackupExpanded = !it.isAddFromBackupExpanded) }
    }

    fun requestRemoveFromBackup(payload: AddressBackupPayload) {
        pendingRemovalPayload = payload
    }

    fun confirmRemoveFromBackup() {
        val payload = pendingRemovalPayload ?: return
        pendingRemovalPayload = null
        viewModelScope.launch {
            deleteAccountFromBackup(payload.address, deleteFromServer = true)
            reloadState()
            val current = stateDelegate.state.value
            if (current.notBackedUp.isEmpty() && current.availableFromBackup.isEmpty()) {
                eventDelegate.sendEvent(ViewEvent.NavigateBack)
            }
        }
    }

    fun cancelRemoveFromBackup() {
        pendingRemovalPayload = null
    }

    fun addAccountToLocal(payload: AddressBackupPayload) {
        viewModelScope.launch {
            when (val result = addBackupAccountToLocal(payload)) {
                is PeraResult.Success -> {
                    reloadState()
                    val current = stateDelegate.state.value
                    if (current.notBackedUp.isEmpty() && current.availableFromBackup.isEmpty()) {
                        eventDelegate.sendEvent(ViewEvent.NavigateBackWithSuccess)
                    } else {
                        eventDelegate.sendEvent(ViewEvent.AddSuccess)
                    }
                }
                is PeraResult.Error -> {
                    reloadState()
                    eventDelegate.sendEvent(ViewEvent.ShowImportError(result.exception.message))
                }
            }
        }
    }

    fun backUpAccount(address: String) {
        viewModelScope.launch {
            addAccountToBackup(address)
            backupSyncManager.syncNow()
            eventDelegate.sendEvent(ViewEvent.BackUpSuccess)
        }
    }

    private suspend fun reloadState() {
        val notBackedUp = getBackupLocalAccounts().filterNot { it.isBackedUp }
        val availableFromBackup = getNotSyncedBackupAccounts()
        stateDelegate.updateState { current ->
            val expanded = if (!initialExpansionApplied) {
                initialExpansionApplied = true
                notBackedUp.isEmpty() && availableFromBackup.isNotEmpty()
            } else {
                current.isAddFromBackupExpanded
            }
            current.copy(
                notBackedUp = notBackedUp,
                availableFromBackup = availableFromBackup,
                isAddFromBackupExpanded = expanded
            )
        }
    }

    private fun observeSyncStatus() {
        backupSyncManager.syncStatus
            .onEach { status ->
                if (status !is BackupSyncStatus.Syncing) {
                    reloadState()
                }
            }
            .launchIn(viewModelScope)
    }

    data class ViewState(
        val notBackedUp: List<BackupLocalAccountItem> = emptyList(),
        val availableFromBackup: List<BackupAccountListItem> = emptyList(),
        val isAddFromBackupExpanded: Boolean = false
    )

    sealed interface ViewEvent {
        data object AddSuccess : ViewEvent
        data object BackUpSuccess : ViewEvent
        data object NavigateBackWithSuccess : ViewEvent
        data object NavigateBack : ViewEvent
        data class ShowImportError(val message: String?) : ViewEvent
    }
}
