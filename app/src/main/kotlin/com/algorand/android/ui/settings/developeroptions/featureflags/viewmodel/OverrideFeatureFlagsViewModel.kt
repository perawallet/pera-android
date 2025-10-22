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

package com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel

import androidx.lifecycle.ViewModel
import com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel.OverrideFeatureFlagsViewModel.ViewState
import com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel.OverrideFeatureFlagsViewModel.ViewState.Content
import com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel.OverrideFeatureFlagsViewModel.ViewState.Idle
import com.algorand.wallet.devoptions.domain.model.DeveloperOptionFeatureFlag
import com.algorand.wallet.devoptions.domain.model.DeveloperOptionFeatureFlag.Status.Overridden
import com.algorand.wallet.devoptions.domain.model.DeveloperOptionFeatureFlag.Status.Remote
import com.algorand.wallet.devoptions.domain.usecase.ClearOverriddenFeatureFlag
import com.algorand.wallet.devoptions.domain.usecase.GetAllDeveloperOptionFeatureFlags
import com.algorand.wallet.devoptions.domain.usecase.OverrideFeatureFlagStatus
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OverrideFeatureFlagsViewModel @Inject constructor(
    private val getAllDeveloperOptionFeatureFlags: GetAllDeveloperOptionFeatureFlags,
    private val overrideFeatureFlagStatus: OverrideFeatureFlagStatus,
    private val clearOverriddenFeatureFlag: ClearOverriddenFeatureFlag,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {


    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun initViewState() {
        stateDelegate.onState<Idle> {
            val featureFlags = getAllDeveloperOptionFeatureFlags()
            stateDelegate.updateState { Content(featureFlags) }
        }
    }

    fun overrideFeatureFlag(featureToggle: FeatureToggle, isEnabled: Boolean) {
        stateDelegate.onState<Content> { content ->
            val updatedFlags = content.featureFlags.map { featureFlag ->
                if (featureFlag.featureToggle == featureToggle) {
                    overrideFeatureFlagStatus(featureToggle.key, isEnabled)
                    featureFlag.copy(status = Overridden(isEnabled))
                } else {
                    featureFlag
                }
            }
            stateDelegate.updateState { Content(updatedFlags) }
        }
    }

    fun toggleOverriddenStatus(featureToggle: FeatureToggle) {
        stateDelegate.onState<Content> { content ->
            val updatedFlags = content.featureFlags.map { featureFlag ->
                if (featureFlag.featureToggle == featureToggle) {
                    when (featureFlag.status) {
                        is Overridden -> {
                            clearOverriddenFeatureFlag(featureToggle.key)
                            featureFlag.copy(status = Remote)
                        }
                        Remote -> {
                            overrideFeatureFlagStatus(featureToggle.key, !featureFlag.remoteValue)
                            featureFlag.copy(status = Overridden(!featureFlag.remoteValue))
                        }
                    }
                } else {
                    featureFlag
                }
            }
            stateDelegate.updateState { Content(updatedFlags) }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val featureFlags: List<DeveloperOptionFeatureFlag>) : ViewState
    }
}
