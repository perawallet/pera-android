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

import android.util.Base64
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.ui.backup.viewmodel.RestoreBackupViewModel.ViewState
import com.algorand.backup.domain.model.Argon2idConfig.Companion.DEFAULT
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
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RestoreBackupViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val restoreBackup: RestoreBackup,
    private val backupSyncManager: BackupSyncManager,
    private val deriveBackupWalletAddress: DeriveBackupWalletAddress
) : ViewModel(),
    StateViewModel<ViewState> by stateDelegate,
    EventViewModel<RestoreBackupViewModel.ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle())
    }

    fun updateMnemonic(mnemonic: String) {
        stateDelegate.onState<ViewState.Idle> { currentState ->
            stateDelegate.updateState { currentState.copy(mnemonic = mnemonic) }
        }
    }

    fun updateSalt(salt: String) {
        stateDelegate.onState<ViewState.Idle> { currentState ->
            stateDelegate.updateState { currentState.copy(salt = salt) }
        }
    }

    fun restoreBackup() {
        stateDelegate.onState<ViewState.Idle> { currentState ->
            if (currentState.mnemonic.isBlank() || currentState.salt.isBlank()) return@onState

            val saltBytes = decodeSalt(currentState.salt) ?: return@onState

            stateDelegate.updateState { ViewState.Loading(mnemonic = currentState.mnemonic, salt = currentState.salt) }

            viewModelScope.launch {
                val walletAddress = deriveBackupWalletAddress(currentState.mnemonic)
                if (walletAddress == null) {
                    resetToIdleWithError(currentState, R.string.backup_restore_failed)
                    return@launch
                }
                val deviceId = DeviceId(UUID.randomUUID().toString())
                performRestore(currentState, saltBytes, deviceId, walletAddress)
            }
        }
    }

    private fun decodeSalt(salt: String): ByteArray? {
        return try {
            Base64.decode(salt, Base64.NO_WRAP)
        } catch (e: IllegalArgumentException) {
            eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowError(R.string.backup_invalid_salt_format))
            null
        }
    }

    private suspend fun performRestore(
        previousState: ViewState.Idle,
        saltBytes: ByteArray,
        deviceId: DeviceId,
        walletAddress: String
    ) {
        when (val result = restoreBackup(previousState.mnemonic, saltBytes, DEFAULT, deviceId, walletAddress)) {
            is PeraResult.Success -> {
                backupSyncManager.enableSync()
                stateDelegate.updateState {
                    ViewState.Success(
                        backupId = result.data.backupId.value,
                        itemCount = result.data.syncState.items.size
                    )
                }
            }
            is PeraResult.Error -> resetToIdleWithError(previousState, R.string.backup_restore_failed)
        }
    }

    private suspend fun resetToIdleWithError(previousState: ViewState.Idle, @StringRes messageResId: Int) {
        stateDelegate.updateState { ViewState.Idle(mnemonic = previousState.mnemonic, salt = previousState.salt) }
        eventDelegate.sendEvent(ViewEvent.ShowError(messageResId))
    }

    sealed interface ViewState {
        data class Idle(val mnemonic: String = "", val salt: String = "") : ViewState
        data class Loading(val mnemonic: String, val salt: String) : ViewState
        data class Success(val backupId: String, val itemCount: Int) : ViewState
    }

    sealed interface ViewEvent {
        data class ShowError(@StringRes val messageResId: Int) : ViewEvent
    }
}
