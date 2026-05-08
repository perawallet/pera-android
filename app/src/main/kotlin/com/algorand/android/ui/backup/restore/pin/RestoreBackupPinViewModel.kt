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

package com.algorand.android.ui.backup.restore.pin

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.ui.backup.restore.pin.RestoreBackupPinViewModel.ViewEvent
import com.algorand.android.ui.backup.restore.pin.RestoreBackupPinViewModel.ViewState
import com.algorand.android.ui.backup.restore.pin.RestoreBackupPinViewModel.ViewState.ContentState
import com.algorand.android.ui.backup.sync.security.BackupSyncPayload
import com.algorand.android.ui.backup.sync.usecase.GetDecryptedBackupSyncQrPayload
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.DeriveBackupWalletAddress
import com.algorand.backup.domain.usecase.RestoreBackup
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PIN_LENGTH = 6

@HiltViewModel
class RestoreBackupPinViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val restoreBackup: RestoreBackup,
    private val backupSyncManager: BackupSyncManager,
    private val getDeviceConfig: GetDeviceConfig,
    private val getDecryptedBackupSyncQrPayload: GetDecryptedBackupSyncQrPayload,
    private val deriveBackupWalletAddress: DeriveBackupWalletAddress,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateViewModel<ViewState> by stateDelegate,
    EventViewModel<ViewEvent> by eventDelegate {

    private val encryptedPayload: String =
        RestoreBackupPinFragmentArgs.fromSavedStateHandle(savedStateHandle).encryptedPayload

    init {
        stateDelegate.setDefaultState(ViewState())
    }

    fun appendDigit(digit: Int) {
        val current = stateDelegate.state.value
        if (current.contentState != ContentState.Idle || current.enteredDigits.length >= PIN_LENGTH) return
        val next = current.enteredDigits + digit.toString()
        stateDelegate.updateState { it.copy(enteredDigits = next) }
        if (next.length == PIN_LENGTH) {
            decryptAndRestore(next)
        }
    }

    fun removeLastDigit() {
        stateDelegate.updateState { current ->
            if (current.contentState != ContentState.Idle || current.enteredDigits.isEmpty()) current
            else current.copy(enteredDigits = current.enteredDigits.dropLast(1))
        }
    }

    private fun decryptAndRestore(pin: String) {
        stateDelegate.updateState { it.copy(contentState = ContentState.Loading) }
        viewModelScope.launch {
            val payloadResult = withContext(Dispatchers.Default) {
                getDecryptedBackupSyncQrPayload(pin, encryptedPayload)
            }
            val payload = payloadResult.getOrElse {
                resetToIdleWithError(R.string.backup_restore_failed)
                return@launch
            }
            restorePayload(payload)
        }
    }

    private suspend fun restorePayload(payload: BackupSyncPayload) {
        val walletAddress = deriveBackupWalletAddress(payload.mnemonic)
            ?: run { resetToIdleWithError(R.string.backup_restore_failed); return }
        val deviceId = DeviceId(getDeviceConfig().deviceId)
        when (restoreBackup(payload.mnemonic, payload.salt, payload.argon2idConfig, deviceId, walletAddress)) {
            is PeraResult.Success -> {
                backupSyncManager.enableSync()
                eventDelegate.sendEvent(ViewEvent.BackupRestored)
            }
            is PeraResult.Error -> resetToIdleWithError(R.string.backup_restore_failed)
        }
    }

    private suspend fun resetToIdleWithError(@StringRes messageResId: Int) {
        stateDelegate.updateState { it.copy(enteredDigits = "", contentState = ContentState.Idle) }
        eventDelegate.sendEvent(ViewEvent.ShowError(messageResId))
    }

    data class ViewState(
        val enteredDigits: String = "",
        val contentState: ContentState = ContentState.Idle
    ) {
        sealed interface ContentState {
            data object Idle : ContentState
            data object Loading : ContentState
        }
    }

    sealed interface ViewEvent {
        data object BackupRestored : ViewEvent
        data class ShowError(@param:StringRes val messageResId: Int) : ViewEvent
    }
}
