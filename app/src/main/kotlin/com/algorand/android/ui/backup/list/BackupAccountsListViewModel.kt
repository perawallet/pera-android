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
import com.algorand.android.ui.backup.list.BackupAccountsListViewModel.ViewState
import com.algorand.android.ui.backup.list.model.BackupLocalAccountItem
import com.algorand.android.ui.backup.list.usecase.GetBackupLocalAccounts
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupAccounts
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BackupAccountsListViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getBackupLocalAccounts: GetBackupLocalAccounts,
    private val getNotSyncedBackupAccounts: GetNotSyncedBackupAccounts
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState())
        loadAccounts()
    }

    fun refresh() {
        loadAccounts()
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

    data class ViewState(
        val localAccounts: List<BackupLocalAccountItem> = emptyList(),
        val notBackedUpCount: Int = 0,
        val availableFromBackupCount: Int = 0
    ) {
        val hasAccountsToReview: Boolean
            get() = notBackedUpCount > 0 || availableFromBackupCount > 0
    }
}
