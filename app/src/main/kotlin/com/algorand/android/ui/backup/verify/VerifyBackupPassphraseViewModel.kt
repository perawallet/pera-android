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

package com.algorand.android.ui.backup.verify

import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.backup.verify.VerifyBackupPassphraseViewModel.ViewEvent
import com.algorand.android.ui.backup.verify.VerifyBackupPassphraseViewModel.ViewState
import com.algorand.android.ui.backup.verify.model.BackupPassphraseValidationOption
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.android.utils.PassphraseKeywordUtils
import com.algorand.android.utils.getOrThrow
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.CreateBackup
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class VerifyBackupPassphraseViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val createBackup: CreateBackup,
    private val backupSyncManager: BackupSyncManager,
    private val getDeviceConfig: GetDeviceConfig,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private val mnemonic: String = savedStateHandle.getOrThrow<String>(MNEMONIC_KEY)
    private val encryptionKey: String = savedStateHandle.getOrThrow<String>(ENCRYPTION_KEY_KEY)

    init {
        stateDelegate.setDefaultState(
            ViewState(
                options = generateOptions(),
                encryptionKey = encryptionKey,
                type = ViewState.Type.Selection
            )
        )
    }

    fun selectOption(optionId: Int, word: String) {
        stateDelegate.updateState { current ->
            if (current.type !is ViewState.Type.Selection) return@updateState current
            current.copy(
                options = current.options.map { option ->
                    if (option.id == optionId) option.copy(selectedOption = word) else option
                }
            )
        }
    }

    fun verifySelections() {
        val current = state.value
        if (current.type !is ViewState.Type.Selection) return
        val isCorrect = current.options.all { it.selectedOption == it.correctWord }
        if (isCorrect) {
            eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowBackupConfirmationDialog)
        } else {
            stateDelegate.updateState { it.copy(options = generateOptions()) }
            eventDelegate.sendEvent(viewModelScope, ViewEvent.IncorrectSelection)
        }
    }

    fun enableCloudBackup() {
        val current = state.value
        if (current.type !is ViewState.Type.Selection) return
        viewModelScope.launch {
            stateDelegate.updateState { it.copy(type = ViewState.Type.CreatingBackup) }
            val deviceId = DeviceId(getDeviceConfig().deviceId)
            val saltBytes = Base64.decode(encryptionKey, Base64.NO_WRAP)
            when (createBackup(mnemonic, deviceId, saltBytes)) {
                is PeraResult.Success -> {
                    backupSyncManager.enableSync()
                    eventDelegate.sendEvent(ViewEvent.BackupCreated)
                }
                is PeraResult.Error -> {
                    stateDelegate.updateState { it.copy(type = ViewState.Type.Selection) }
                    eventDelegate.sendEvent(ViewEvent.BackupCreationFailed)
                }
            }
        }
    }

    private fun generateOptions(): List<BackupPassphraseValidationOption> {
        return PassphraseKeywordUtils.generatePassphraseValidationItems(
            words = mnemonic.split(" "),
            itemCount = ITEM_COUNT,
            perItemCount = OPTIONS_PER_ITEM
        ).mapIndexed { index, item ->
            BackupPassphraseValidationOption(
                id = index,
                wordPosition = item.correctWordIndex + 1,
                correctWord = item.correctWord,
                options = item.options,
                selectedOption = null
            )
        }
    }

    data class ViewState(
        val options: List<BackupPassphraseValidationOption>,
        val encryptionKey: String,
        val type: Type
    ) {
        val isProceedEnabled: Boolean
            get() = type is Type.Selection && options.isNotEmpty() && options.all { it.selectedOption != null }

        sealed interface Type {
            data object Selection : Type
            data object CreatingBackup : Type
        }
    }

    sealed interface ViewEvent {
        data object ShowBackupConfirmationDialog : ViewEvent
        data object IncorrectSelection : ViewEvent
        data object BackupCreated : ViewEvent
        data object BackupCreationFailed : ViewEvent
    }

    private companion object {
        const val MNEMONIC_KEY = "mnemonic"
        const val ENCRYPTION_KEY_KEY = "encryptionKey"
        const val ITEM_COUNT = 3
        const val OPTIONS_PER_ITEM = 3
    }
}
