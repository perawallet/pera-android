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

package com.algorand.android.ui.backup.credentials

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.backup.CreateBackupFile
import com.algorand.android.ui.backup.credentials.BackupCredentialsViewModel.ViewEvent
import com.algorand.android.ui.backup.credentials.BackupCredentialsViewModel.ViewState
import com.algorand.backup.domain.usecase.RevealBackupAuthCredentials
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.utils.date.TimeProvider
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
class BackupCredentialsViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val revealBackupAuthCredentials: RevealBackupAuthCredentials,
    private val createBackupFile: CreateBackupFile,
    private val timeProvider: TimeProvider
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private var exportJob: Job? = null

    init {
        stateDelegate.setDefaultState(loadCredentials())
    }

    fun exportBackupFile() {
        if (exportJob?.isActive == true) return
        exportJob = viewModelScope.launch(Dispatchers.Default) {
            when (val result = createBackupFile()) {
                is PeraResult.Success -> eventDelegate.sendEvent(
                    ViewEvent.BackupFileReady(
                        fileContent = result.data,
                        fileName = createExportFileName()
                    )
                )
                is PeraResult.Error -> eventDelegate.sendEvent(ViewEvent.BackupFileCreationFailed)
            }
        }
    }

    private fun loadCredentials(): ViewState {
        val result = revealBackupAuthCredentials { backupId, mnemonic, salt ->
            ViewState.Content(
                credentialAddress = backupId.address,
                mnemonic = String(mnemonic.reveal(), Charsets.UTF_8),
                encryptionKey = Base64.encodeToString(salt, Base64.NO_WRAP)
            )
        }
        return when (result) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> ViewState.Error
        }
    }

    sealed interface ViewState {
        data object Error : ViewState
        data class Content(
            val credentialAddress: String,
            val mnemonic: String,
            val encryptionKey: String
        ) : ViewState
    }

    private fun createExportFileName(): String {
        val date = timeProvider.getZonedDateTimeNow().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return "pera-backup-key-$date.txt"
    }

    sealed interface ViewEvent {
        data class BackupFileReady(
            val fileContent: String,
            val fileName: String
        ) : ViewEvent
        data object BackupFileCreationFailed : ViewEvent
    }
}
