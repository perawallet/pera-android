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
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewEvent
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewState
import com.algorand.backup.domain.usecase.DisableBackup
import com.algorand.backup.domain.usecase.GetBackupId
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BackupOverviewViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    getBackupId: GetBackupId,
    private val disableBackup: DisableBackup
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        val backupId = getBackupId()?.value.orEmpty()
        stateDelegate.setDefaultState(ViewState(backupId = backupId))
    }

    fun disableBackup() {
        viewModelScope.launch {
            disableBackup.invoke()
            eventDelegate.sendEvent(ViewEvent.NavigateBack)
        }
    }

    data class ViewState(val backupId: String)

    sealed interface ViewEvent {
        data object NavigateBack : ViewEvent
    }
}
