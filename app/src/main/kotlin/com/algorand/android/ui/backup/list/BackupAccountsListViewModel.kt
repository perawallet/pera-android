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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.backup.list.BackupAccountsListViewModel.ViewEvent
import com.algorand.android.ui.backup.list.BackupAccountsListViewModel.ViewState
import com.algorand.android.ui.backup.list.model.BackupAccountListItem
import com.algorand.android.ui.backup.list.model.BackupListTab
import com.algorand.android.ui.backup.list.usecase.AddBackupAccountToLocal
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupAccounts
import com.algorand.android.ui.backup.list.usecase.GetSyncedBackupAccounts
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BackupAccountsListViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getSyncedBackupAccounts: GetSyncedBackupAccounts,
    private val getNotSyncedBackupAccounts: GetNotSyncedBackupAccounts,
    private val addBackupAccountToLocal: AddBackupAccountToLocal,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        val initialTab = savedStateHandle.get<BackupListTab>(SELECTED_TAB_KEY) ?: BackupListTab.SYNCED
        stateDelegate.setDefaultState(ViewState(selectedTab = initialTab))
        loadAccounts()
    }

    fun selectTab(tab: BackupListTab) {
        stateDelegate.updateState { it.copy(selectedTab = tab) }
    }

    fun addAccountToLocal(payload: AddressBackupPayload) {
        viewModelScope.launch {
            when (val result = addBackupAccountToLocal(payload)) {
                is PeraResult.Success -> loadAccounts()
                is PeraResult.Error -> {
                    loadAccounts()
                    eventDelegate.sendEvent(ViewEvent.ShowImportError(result.exception.message))
                }
            }
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            val synced = getSyncedBackupAccounts()
            val notSynced = getNotSyncedBackupAccounts()
            stateDelegate.updateState { it.copy(synced = synced, notSynced = notSynced) }
        }
    }

    data class ViewState(
        val selectedTab: BackupListTab = BackupListTab.SYNCED,
        val synced: List<BackupAccountListItem> = emptyList(),
        val notSynced: List<BackupAccountListItem> = emptyList()
    )

    sealed interface ViewEvent {
        data class ShowImportError(val message: String?) : ViewEvent
    }

    private companion object {
        const val SELECTED_TAB_KEY = "selectedTab"
    }
}
