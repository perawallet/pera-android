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

package com.algorand.android.modules.addaccount.intro.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.intro.domain.model.AddAccountIntroPreview
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateAlgo25Account
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateHdKeyAccount
import com.algorand.android.modules.addaccount.intro.domain.usecase.GetAddAccountIntroPreview
import com.algorand.android.modules.tracking.onboarding.register.registerintro.RegisterIntroFragmentEventTracker
import com.algorand.android.usecase.RegistrationUseCase
import com.algorand.android.utils.getOrElse
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAccountIntroViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAddAccountIntroPreview: GetAddAccountIntroPreview,
    private val registrationUseCase: RegistrationUseCase,
    private val createHdKeyAccount: CreateHdKeyAccount,
    private val createAlgo25Account: CreateAlgo25Account,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val peraEventTracker: PeraEventTracker,
    private val registerIntroFragmentEventTracker: RegisterIntroFragmentEventTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<AddAccountIntroViewModel.ViewState> by stateDelegate {

    private val isShowingCloseButton = savedStateHandle.getOrElse(IS_SHOWING_CLOSE_BUTTON_KEY, false)

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
        initializePreview()
    }

    private fun initializePreview() {
        viewModelScope.launch {
            getAddAccountIntroPreview(isShowingCloseButton).collectLatest { preview ->
                stateDelegate.updateState {
                    ViewState.Content(preview)
                }
            }
        }
    }

    fun setRegisterSkip() {
        registrationUseCase.setRegistrationSkipPreferenceAsSkipped()
    }

    fun logOnboardingWelcomeAccountCreateClickEvent() {
        viewModelScope.launch {
            registerIntroFragmentEventTracker.logOnboardingCreateNewAccountEventTracker()
        }
    }

    fun logOnboardingWelcomeAccountRecoverClickEvent() {
        viewModelScope.launch {
            registerIntroFragmentEventTracker.logOnboardingWelcomeAccountRecoverEvent()
        }
    }

    fun createHdKeyAccount(): Result<AccountCreation> = createHdKeyAccount.invoke()

    suspend fun createAlgo25Account(): Result<AccountCreation> = createAlgo25Account.invoke()

    fun isJointAccountFeatureEnabled(): Boolean {
        return isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)
    }

    fun logEvent(eventName: String) {
        viewModelScope.launch {
            peraEventTracker.logEvent(eventName)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val preview: AddAccountIntroPreview
        ) : ViewState
    }

    companion object {
        private const val IS_SHOWING_CLOSE_BUTTON_KEY = "isShowingCloseButton"
    }
}
