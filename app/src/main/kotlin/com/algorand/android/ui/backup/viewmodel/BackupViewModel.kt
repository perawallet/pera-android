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

package com.algorand.android.ui.backup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.backup.viewmodel.BackupViewModel.ViewState
import com.algorand.backup.domain.usecase.DisableBackup
import com.algorand.backup.domain.usecase.GetBackupId
import com.algorand.backup.domain.usecase.HasBackup
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val hasBackup: HasBackup,
    private val getBackupId: GetBackupId,
    private val disableBackup: DisableBackup
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        checkBackupStatus()
    }

    private fun checkBackupStatus() {
        if (hasBackup()) {
            val backupId = getBackupId()?.value.orEmpty()
            stateDelegate.updateState { ViewState.Active(backupId = backupId) }
        } else {
            stateDelegate.updateState { ViewState.Selection }
        }
    }

    fun createBackup() {
        stateDelegate.updateState { ViewState.Create }
    }

    fun restoreBackup() {
        stateDelegate.updateState { ViewState.Restore }
    }

    fun navigateBack() {
        checkBackupStatus()
    }

    fun disableBackup() {
        viewModelScope.launch {
            disableBackup.invoke()
            stateDelegate.updateState { ViewState.Selection }
        }
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data object Selection : ViewState
        data class Active(val backupId: String) : ViewState
        data object Create : ViewState
        data object Restore : ViewState
    }
}
