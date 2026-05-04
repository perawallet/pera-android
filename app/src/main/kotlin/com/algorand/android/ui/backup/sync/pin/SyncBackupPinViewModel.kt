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

package com.algorand.android.ui.backup.sync.pin

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.ui.backup.sync.pin.SyncBackupPinViewModel.ViewEvent
import com.algorand.android.ui.backup.sync.pin.SyncBackupPinViewModel.ViewState
import com.algorand.android.ui.backup.sync.pin.SyncBackupPinViewModel.ViewState.ContentState
import com.algorand.android.ui.backup.sync.security.BackupSyncPayload
import com.algorand.android.ui.backup.sync.usecase.GetEncryptedBackupSyncQrPayload
import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.usecase.RevealBackupAuthCredentials
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
class SyncBackupPinViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val revealBackupAuthCredentials: RevealBackupAuthCredentials,
    private val getEncryptedBackupSyncQrPayload: GetEncryptedBackupSyncQrPayload
) : ViewModel(),
    StateViewModel<ViewState> by stateDelegate,
    EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState())
    }

    fun appendDigit(digit: Int) {
        val current = stateDelegate.state.value
        if (current.contentState != ContentState.Idle || current.enteredDigits.length >= PIN_LENGTH) return
        val next = current.enteredDigits + digit.toString()
        stateDelegate.updateState { it.copy(enteredDigits = next) }
        if (next.length == PIN_LENGTH) {
            encryptAndProceed(next)
        }
    }

    fun removeLastDigit() {
        stateDelegate.updateState { current ->
            if (current.contentState != ContentState.Idle || current.enteredDigits.isEmpty()) current
            else current.copy(enteredDigits = current.enteredDigits.dropLast(1))
        }
    }

    private fun encryptAndProceed(pin: String) {
        stateDelegate.updateState { it.copy(contentState = ContentState.Loading) }
        viewModelScope.launch {
            val payloadResult = readPayload()
            if (payloadResult is PeraResult.Error) {
                stateDelegate.updateState { it.copy(enteredDigits = "", contentState = ContentState.Idle) }
                eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowError(R.string.an_error_occurred))
                return@launch
            }
            val payload = (payloadResult as PeraResult.Success).data
            val encrypted = withContext(Dispatchers.Default) {
                getEncryptedBackupSyncQrPayload(pin, payload)
            }
            eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToQr(encrypted))
        }
    }

    private fun readPayload(): PeraResult<BackupSyncPayload> {
        return revealBackupAuthCredentials { _, mnemonic, salt ->
            BackupSyncPayload(
                mnemonic = String(mnemonic.reveal(), Charsets.UTF_8),
                salt = salt,
                argon2idConfig = Argon2idConfig.DEFAULT
            )
        }
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
        data class NavigateToQr(val encryptedPayload: String) : ViewEvent
        data class ShowError(@param:StringRes val messageResId: Int) : ViewEvent
    }
}
