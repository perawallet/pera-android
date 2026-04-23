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
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel.ViewEvent
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel.ViewState
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.SyncBackupResult
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.CreateBackup
import com.algorand.backup.domain.usecase.SyncBackup
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CreateBackupViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val createBackup: CreateBackup,
    private val syncBackup: SyncBackup,
    private val backupSyncManager: BackupSyncManager,
    private val getDeviceConfig: GetDeviceConfig,
    bip39WalletProvider: Bip39WalletProvider,
    peraBip39Sdk: PeraBip39Sdk
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        val wallet = bip39WalletProvider.createBip39Wallet()
        val entropy = wallet.getEntropy().value
        val mnemonic = peraBip39Sdk.getMnemonicFromEntropy(entropy)
        if (mnemonic != null) {
            stateDelegate.setDefaultState(ViewState.MnemonicGenerated(mnemonic = mnemonic))
        } else {
            stateDelegate.setDefaultState(ViewState.Error)
        }
    }

    fun confirmBackup() {
        stateDelegate.onState<ViewState.MnemonicGenerated> { currentState ->
            stateDelegate.updateState { ViewState.Loading(mnemonic = currentState.mnemonic) }
            viewModelScope.launch {
                val deviceId = DeviceId(getDeviceConfig().deviceId)
                when (val result = createBackup(currentState.mnemonic, deviceId)) {
                    is PeraResult.Success -> {
                        val backup = result.data
                        val salt = Base64.encodeToString(backup.salt, Base64.NO_WRAP)
                        backupSyncManager.enableSync()
                        stateDelegate.updateState {
                            ViewState.Syncing(backupId = backup.backupId.value, salt = salt)
                        }
                        runSync(backup.backupId.value, salt)
                    }
                    is PeraResult.Error -> {
                        stateDelegate.updateState {
                            ViewState.MnemonicGenerated(mnemonic = currentState.mnemonic)
                        }
                        eventDelegate.sendEvent(ViewEvent.ShowError(R.string.backup_creation_failed))
                    }
                }
            }
        }
    }

    private suspend fun runSync(backupId: String, salt: String) {
        when (syncBackup()) {
            is SyncBackupResult.Success,
            is SyncBackupResult.SuccessWithPendingChanges,
            is SyncBackupResult.AlreadyRunning -> {
                stateDelegate.updateState {
                    ViewState.Success(backupId = backupId, salt = salt)
                }
            }
            is SyncBackupResult.Error -> {
                stateDelegate.updateState {
                    ViewState.Success(backupId = backupId, salt = salt)
                }
                eventDelegate.sendEvent(ViewEvent.ShowError(R.string.backup_sync_failed))
            }
        }
    }

    sealed interface ViewState {
        data object Error : ViewState
        data class MnemonicGenerated(val mnemonic: String) : ViewState
        data class Loading(val mnemonic: String) : ViewState
        data class Syncing(val backupId: String, val salt: String) : ViewState
        data class Success(val backupId: String, val salt: String) : ViewState
    }

    sealed interface ViewEvent {
        data class ShowError(@param:StringRes val messageResId: Int) : ViewEvent
    }

    companion object {
        private const val LOG_TAG = "CreateBackupViewModel"
    }
}
