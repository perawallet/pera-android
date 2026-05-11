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
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateAlgo25Account
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateHdKeyAccount
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.modules.tracking.onboarding.register.registerintro.RegisterIntroFragmentEventTracker
import com.algorand.android.usecase.RegistrationUseCase
import com.algorand.android.utils.getOrElse
import com.algorand.wallet.account.local.domain.usecase.GetHasAnyHdSeedId
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList")
class AddAccountIntroViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val hasAnyHdSeedId: GetHasAnyHdSeedId,
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount,
    private val registrationUseCase: RegistrationUseCase,
    private val createHdKeyAccount: CreateHdKeyAccount,
    private val createAlgo25Account: CreateAlgo25Account,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val peraEventTracker: PeraEventTracker,
    private val registerIntroFragmentEventTracker: RegisterIntroFragmentEventTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateViewModel<AddAccountIntroViewModel.ViewState> by stateDelegate,
    EventViewModel<AddAccountIntroViewModel.ViewEvent> by eventDelegate {

    private val isShowingCloseButton = savedStateHandle.getOrElse(IS_SHOWING_CLOSE_BUTTON_KEY, false)

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        initializeState()
    }

    private fun initializeState() {
        viewModelScope.launch {
            val hasHdWallet = hasAnyHdSeedId()
            val hasLocalAccount = isThereAnyLocalAccount()
            val isSkipButtonVisible = !hasLocalAccount
            val isJointAccountFeatureEnabled = isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)

            stateDelegate.updateState {
                ViewState.Content(
                    isSkipButtonVisible = isSkipButtonVisible,
                    isCloseButtonVisible = isShowingCloseButton,
                    primaryAccountOption = if (hasHdWallet) {
                        PrimaryAccountOption.AddAccount
                    } else {
                        PrimaryAccountOption.CreateUniversalWallet
                    },
                    jointAccountOption = if (isJointAccountFeatureEnabled) {
                        JointAccountOption.Visible
                    } else {
                        JointAccountOption.Hidden
                    }
                )
            }
        }
    }

    private fun setRegisterSkip() {
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

    fun onCreateAlgo25AccountClicked() {
        logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_WALLET)
        viewModelScope.launch {
            when (val result = createAlgo25Account()) {
                is Result.Success -> {
                    eventDelegate.sendEvent(ViewEvent.NavigateToNameRegistration(result.data))
                }

                is Result.Error -> {
                    eventDelegate.sendEvent(ViewEvent.ShowError)
                }
            }
        }
    }

    private suspend fun createAlgo25Account(): Result<AccountCreation> = createAlgo25Account.invoke()

    fun logEvent(eventName: String) {
        viewModelScope.launch {
            peraEventTracker.logEvent(eventName)
        }
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data class Content(
            val isSkipButtonVisible: Boolean,
            val isCloseButtonVisible: Boolean,
            val primaryAccountOption: PrimaryAccountOption,
            val jointAccountOption: JointAccountOption
        ) : ViewState
    }

    sealed interface PrimaryAccountOption {
        data object AddAccount : PrimaryAccountOption
        data object CreateUniversalWallet : PrimaryAccountOption
    }

    sealed interface JointAccountOption {
        data object Visible : JointAccountOption
        data object Hidden : JointAccountOption
    }

    fun onSkipClicked() {
        viewModelScope.launch {
            peraEventTracker.logEvent(PeraClickEvent.TAP_ONBOARDING_WELCOME_SKIP)
            setRegisterSkip()
            eventDelegate.sendEvent(ViewEvent.NavigateToHome)
        }
    }

    sealed interface ViewEvent {
        data object NavigateToHome : ViewEvent
        data class NavigateToNameRegistration(val accountCreation: AccountCreation) : ViewEvent
        data object ShowError : ViewEvent
    }

    companion object {
        private const val IS_SHOWING_CLOSE_BUTTON_KEY = "isShowingCloseButton"
    }
}
