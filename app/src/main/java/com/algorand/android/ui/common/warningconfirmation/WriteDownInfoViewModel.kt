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

package com.algorand.android.ui.common.warningconfirmation

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.tracking.onboarding.register.OnboardingCreateAccountReadyEventTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class WriteDownInfoViewModel @Inject constructor(
    private val onboardingCreateAccountReadyEventTracker: OnboardingCreateAccountReadyEventTracker
) : BaseViewModel() {

    fun logOnboardingReadyToBeginClickEvent() {
        viewModelScope.launch {
            onboardingCreateAccountReadyEventTracker.logOnboardingCreateAccountReadyEvent()
        }
    }
}
