/*
 *  Copyright 2022-2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.createaccount.result

import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.modules.tracking.onboarding.register.createaccountresultinfo.CreateAccountResultInfoFragmentEventTracker
import com.algorand.android.usecase.LockPreferencesUseCase
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountCount
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class CreateAccountResultInfoViewModel @Inject constructor(
    private val getLocalAccountCount: GetLocalAccountCount,
    private val lockPreferencesUseCase: LockPreferencesUseCase,
    private val createAccountResultInfoFragmentEventTracker: CreateAccountResultInfoFragmentEventTracker,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<CreateAccountResultInfoViewModel.ViewState> by stateDelegate {

    init {
        initViewState()
    }

    private fun initViewState() {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun setDefaultState() {
        viewModelScope.launchIO {
            val isItFirstAccountThatAdded = getLocalAccountCount() == 1

            val titleTextRes = R.string.account_has_been_added

            val descriptionTextRes = if (isItFirstAccountThatAdded) {
                R.string.welcome_to_pera_your_account
            } else {
                R.string.congratulations_your_account
            }

            val firstButtonTextRes = R.string.buy_algo

            val secondButtonTextRes =
                if (isItFirstAccountThatAdded) R.string.start_using_pera else R.string.continue_text

            val defaultState = ViewState.DefaultState(
                titleTextRes = titleTextRes,
                descriptionTextRes = descriptionTextRes,
                firstButtonTextRes = firstButtonTextRes,
                secondButtonTextRes = secondButtonTextRes
            )

            stateDelegate.updateState { defaultState }
        }
    }

    fun shouldForceLockNavigation(): Boolean {
        return lockPreferencesUseCase.shouldNavigateLockNavigation()
    }

    fun logOnboardingBuyAlgoClickEvent() {
        viewModelScope.launch {
            createAccountResultInfoFragmentEventTracker.logOnboardingAccountVerifiedBuyAlgoEvent()
        }
    }

    fun logOnboardingStartUsingPeraClickEvent() {
        viewModelScope.launch {
            createAccountResultInfoFragmentEventTracker.logOnboardingAccountVerifiedStartPeraEvent()
        }
    }

    sealed class ViewState {
        data object Idle : ViewState()

        data class DefaultState(
            val titleTextRes: Int,
            val descriptionTextRes: Int,
            val firstButtonTextRes: Int,
            val secondButtonTextRes: Int
        ) : ViewState()
    }
}
