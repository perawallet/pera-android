/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.lock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.autolockmanager.ui.AutoLockManager
import com.algorand.android.modules.lock.ui.LockViewModel.ViewEvent
import com.algorand.android.usecase.DeleteAllDataUseCase
import com.algorand.android.usecase.LockUseCase
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LockViewModel @Inject constructor(
    private val deleteAllDataUseCase: DeleteAllDataUseCase,
    private val lockUseCase: LockUseCase,
    private val autoLockManager: AutoLockManager,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

    fun deleteAllData() {
        viewModelScope.launch {
            deleteAllDataUseCase.deleteAllData()
            eventDelegate.sendEvent(ViewEvent.RestartApp)
        }
    }

    fun shouldShowBiometricDialog(): Boolean {
        return lockUseCase.shouldShowBiometricDialog()
    }

    fun getCurrentPassword(): String? {
        return lockUseCase.getCurrentPassword()
    }

    fun getLockPenaltyRemainingTime(): Long {
        return lockUseCase.getLockPenaltyRemainingTime()
    }

    fun getLockAttemptCount(): Int {
        return lockUseCase.getLockAttemptCount()
    }

    fun setLockAttemptCount(lockAttemptCount: Int) {
        lockUseCase.setLockAttemptCount(lockAttemptCount)
    }

    fun setLockPenaltyRemainingTime(penaltyRemainingTime: Long) {
        lockUseCase.setLockPenaltyRemainingTime(penaltyRemainingTime)
    }

    fun onAuthSucceed() {
        autoLockManager.onAppUnlocked()
    }

    sealed interface ViewEvent {
        data object RestartApp : ViewEvent
    }
}
