/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.onboarding.recoverypassphrase.qrscanner.ui

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.launchIO
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoverWithPassphraseQrScannerViewModel @Inject constructor(
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
) : BaseViewModel(), EventViewModel<RecoverWithPassphraseQrScannerViewModel.ViewEvent> by eventDelegate {

    fun onImportAccountDeepLink(mnemonic: String) {
        viewModelScope.launchIO {
            eventDelegate.sendEvent(
                if (isAccountLimitExceedUseCase.isAccountLimitExceed()) {
                    ViewEvent.ShowMaxAccountLimitExceededError
                } else {
                    ViewEvent.NavToRecoverWithPassphraseNavigation(mnemonic)
                }
            )
        }
    }

    sealed interface ViewEvent {
        data class NavToRecoverWithPassphraseNavigation(val mnemonic: String) : ViewEvent
        data object ShowMaxAccountLimitExceededError : ViewEvent
    }
}
