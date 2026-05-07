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

package com.algorand.android.ui.backup.restore.encryptionkey

import android.util.Base64
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.ui.backup.restore.encryptionkey.RestoreBackupEncryptionKeyViewModel.ViewEvent
import com.algorand.android.ui.backup.restore.encryptionkey.RestoreBackupEncryptionKeyViewModel.ViewState
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.android.utils.getOrThrow
import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.model.BackupMnemonicMismatchException
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.security.Argon2idEncoder
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.RestoreBackup
import com.algorand.backup.domain.usecase.ValidateBackupMnemonicForAddress
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class RestoreBackupEncryptionKeyViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val restoreBackup: RestoreBackup,
    private val backupSyncManager: BackupSyncManager,
    private val getDeviceConfig: GetDeviceConfig,
    private val argon2idEncoder: Argon2idEncoder,
    private val validateBackupMnemonicForAddress: ValidateBackupMnemonicForAddress,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateViewModel<ViewState> by stateDelegate,
    EventViewModel<ViewEvent> by eventDelegate {

    private val mnemonic: String = savedStateHandle.getOrThrow(MNEMONIC_KEY)
    private val backupAddress: String? = savedStateHandle[BACKUP_ADDRESS_KEY]
    private val decodedHash = savedStateHandle.get<String>(ENCODED_HASH_KEY)?.let { encoded ->
        argon2idEncoder.decode(encoded).getDataOrNull()
    }

    init {
        val initialKey = decodedHash?.let { Base64.encodeToString(it.salt, Base64.NO_WRAP) }
        stateDelegate.setDefaultState(ViewState(encryptionKey = initialKey.orEmpty(), isRestoring = false))
        if (initialKey != null) {
            restore()
        }
    }

    fun updateEncryptionKey(value: String) {
        stateDelegate.updateState { current ->
            if (current.isRestoring) current else current.copy(encryptionKey = value)
        }
    }

    fun restore() {
        val current = state.value
        if (current.isRestoring || current.encryptionKey.isBlank()) return

        val saltBytes = try {
            Base64.decode(current.encryptionKey, Base64.NO_WRAP)
        } catch (e: IllegalArgumentException) {
            eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowError(R.string.backup_invalid_salt_format))
            return
        }

        stateDelegate.updateState { it.copy(isRestoring = true) }

        viewModelScope.launch(Dispatchers.Default) {
            if (backupAddress != null && decodedHash != null) {
                val validationResult = validateBackupMnemonicForAddress(mnemonic, decodedHash, backupAddress)
                if (validationResult is PeraResult.Error) {
                    stateDelegate.updateState { it.copy(isRestoring = false) }
                    val errorRes = if (validationResult.exception is BackupMnemonicMismatchException) {
                        R.string.passphrase_does_not_match_backup_file
                    } else {
                        R.string.backup_restore_failed
                    }
                    eventDelegate.sendEvent(ViewEvent.ShowError(errorRes))
                    return@launch
                }
            }

            val deviceId = DeviceId(getDeviceConfig().deviceId)
            when (restoreBackup(mnemonic, saltBytes, Argon2idConfig.DEFAULT, deviceId)) {
                is PeraResult.Success -> {
                    backupSyncManager.enableSync()
                    eventDelegate.sendEvent(ViewEvent.BackupRestored)
                }
                is PeraResult.Error -> {
                    stateDelegate.updateState { it.copy(isRestoring = false) }
                    eventDelegate.sendEvent(ViewEvent.ShowError(R.string.backup_restore_failed))
                }
            }
        }
    }

    data class ViewState(val encryptionKey: String, val isRestoring: Boolean) {
        val isProceedEnabled: Boolean
            get() = !isRestoring && encryptionKey.isNotBlank()
    }

    sealed interface ViewEvent {
        data object BackupRestored : ViewEvent
        data class ShowError(@StringRes val messageResId: Int) : ViewEvent
    }

    private companion object {
        const val MNEMONIC_KEY = "mnemonic"
        const val ENCODED_HASH_KEY = "encodedHash"
        const val BACKUP_ADDRESS_KEY = "backupAddress"
    }
}
