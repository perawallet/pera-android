/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.recoveraccounttypeselection

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.usecase.IsOnHdWalletUseCase
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.preference.setRegisterSkip
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountRecoveryTypeSelectionViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount,
    private val stateDelegate: StateDelegate<ViewState>,
    private val isOnHdWalletUseCase: IsOnHdWalletUseCase
) : BaseViewModel(), StateViewModel<AccountRecoveryTypeSelectionViewModel.ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun setRegisterSkip() {
        sharedPref.setRegisterSkip()
    }

    fun setupToolbar() {
        viewModelScope.launchIO {
            if (!isThereAnyLocalAccount()) {
                stateDelegate.setDefaultState(ViewState.NoLocalAccountState)
            }
        }
    }

    fun isOnHdWallet(): Boolean {
        return isOnHdWalletUseCase.invoke()
    }

    fun logRecoverAccountTypeClickEvent(onboardingAccountType: OnboardingAccountType) {
        val clickEvent = when (onboardingAccountType) {
            OnboardingAccountType.HdKey -> PeraClickEvent.TAP_ONBOARDING_RECOVER_UNIVERSAL
            OnboardingAccountType.Algo25 -> PeraClickEvent.TAP_ONBOARDING_RECOVER_ALGO25
        }
        logEvent(clickEvent)
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object NoLocalAccountState : ViewState
    }
}
