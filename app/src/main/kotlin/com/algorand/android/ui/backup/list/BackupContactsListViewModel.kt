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
import com.algorand.android.ui.backup.list.BackupContactsListViewModel.ViewState
import com.algorand.android.ui.backup.list.model.BackupContactListItem
import com.algorand.android.ui.backup.list.model.BackupListTab
import com.algorand.android.ui.backup.list.usecase.AddBackupContactToLocal
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupContacts
import com.algorand.android.ui.backup.list.usecase.GetSyncedBackupContacts
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BackupContactsListViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getSyncedBackupContacts: GetSyncedBackupContacts,
    private val getNotSyncedBackupContacts: GetNotSyncedBackupContacts,
    private val addBackupContactToLocal: AddBackupContactToLocal,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        val initialTab = savedStateHandle.get<BackupListTab>(SELECTED_TAB_KEY) ?: BackupListTab.SYNCED
        stateDelegate.setDefaultState(ViewState(selectedTab = initialTab))
        loadContacts()
    }

    fun selectTab(tab: BackupListTab) {
        stateDelegate.updateState { it.copy(selectedTab = tab) }
    }

    fun addContactToLocal(payload: ContactBackupPayload) {
        viewModelScope.launch {
            addBackupContactToLocal(payload)
            loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            val synced = getSyncedBackupContacts()
            val notSynced = getNotSyncedBackupContacts()
            stateDelegate.updateState { it.copy(synced = synced, notSynced = notSynced) }
        }
    }

    data class ViewState(
        val selectedTab: BackupListTab = BackupListTab.SYNCED,
        val synced: List<BackupContactListItem> = emptyList(),
        val notSynced: List<BackupContactListItem> = emptyList()
    )

    private companion object {
        const val SELECTED_TAB_KEY = "selectedTab"
    }
}
