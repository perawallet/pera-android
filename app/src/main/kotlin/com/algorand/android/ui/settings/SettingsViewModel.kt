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

package com.algorand.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent.ShowDataClearedBottomSheet
import com.algorand.android.ui.settings.model.SettingsPreview
import com.algorand.android.ui.settings.usecase.SettingsPreviewUseCase
import com.algorand.android.usecase.DeleteAllDataUseCase
import com.algorand.android.utils.launchIO
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.HasBackup
import com.algorand.wallet.devoptions.domain.usecase.EnableDeveloperOptions
import com.algorand.wallet.devoptions.domain.usecase.IsDeveloperOptionsEnabled
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.utils.ClickCounter
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAllDataUseCase: DeleteAllDataUseCase,
    private val settingsPreviewUseCase: SettingsPreviewUseCase,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val isDeveloperOptionsEnabled: IsDeveloperOptionsEnabled,
    private val enableDeveloperOps: EnableDeveloperOptions,
    private val settingsEventTracker: SettingsEventTracker,
    private val hasBackup: HasBackup,
    backupSyncManager: BackupSyncManager
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

    private val _settingsPreviewFlow = MutableStateFlow<SettingsPreview?>(null)
    val settingsPreviewFlow: StateFlow<SettingsPreview?> get() = _settingsPreviewFlow

    private val _isCloudBackupEnabled = MutableStateFlow(hasBackup())
    val isCloudBackupEnabledFlow: StateFlow<Boolean> get() = _isCloudBackupEnabled

    init {
        viewModelScope.launch {
            backupSyncManager.syncStatus.collect {
                _isCloudBackupEnabled.value = hasBackup()
            }
        }
    }

    fun refreshCloudBackupState() {
        _isCloudBackupEnabled.value = hasBackup()
    }

    private val devOptionsClickCounter = ClickCounter(onClick = ::processDevOptionsClick)

    fun deleteAllData() {
        viewModelScope.launch {
            deleteAllDataUseCase.deleteAllData()
            eventDelegate.sendEvent(ShowDataClearedBottomSheet)
        }
    }

    fun initSettingsPreviewFlow() {
        viewModelScope.launchIO {
            settingsPreviewUseCase.getSettingsPreviewFlow().collectLatest { preview ->
                _settingsPreviewFlow.emit(preview)
            }
        }
    }

    fun isPasskeysFeatureEnabled(): Boolean = isFeatureToggleEnabled(FeatureToggle.LIQUID_AUTH.key)

    fun isBackupFeatureEnabled(): Boolean = isFeatureToggleEnabled(FeatureToggle.BACKUP.key)

    fun isBackupEnabled(): Boolean = hasBackup()

    fun enableDeveloperOptions() {
        devOptionsClickCounter.click()
    }

    fun onPasskeysClick() {
        viewModelScope.launch {
            settingsEventTracker.logPasskeysClickEvent()
            eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToPasskeys)
        }
    }

    private fun processDevOptionsClick(clickCount: Int) {
        when {
            clickCount == DEV_OPTIONS_ENABLE_CLICK_THRESHOLD -> {
                if (isDeveloperOptionsEnabled()) {
                    eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowDevOptionsAlreadyEnabled)
                } else {
                    enableDeveloperOps()
                    eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowDevOptionsEnabled)
                }
                devOptionsClickCounter.reset()
            }

            clickCount >= DEV_OPTIONS_INFO_CLICK_THRESHOLD -> {
                val remainingClicks = DEV_OPTIONS_ENABLE_CLICK_THRESHOLD - clickCount
                eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowRemainingClicksToDevOptions(remainingClicks))
            }
        }
    }

    sealed interface ViewEvent {
        data object ShowDataClearedBottomSheet : ViewEvent
        data class ShowRemainingClicksToDevOptions(val remainingClicks: Int) : ViewEvent
        data object ShowDevOptionsEnabled : ViewEvent
        data object ShowDevOptionsAlreadyEnabled : ViewEvent
        data object NavigateToPasskeys : ViewEvent
    }

    private companion object {
        const val DEV_OPTIONS_INFO_CLICK_THRESHOLD = 7
        const val DEV_OPTIONS_ENABLE_CLICK_THRESHOLD = 10
    }
}
