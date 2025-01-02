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

package com.algorand.android

import androidx.lifecycle.ViewModel
import com.algorand.android.BuildConfig.DISCOVER_BROWSE_DAPP_MAINNET_URL
import com.algorand.android.BuildConfig.DISCOVER_BROWSE_DAPP_TESTNET_URL
import com.algorand.android.BuildConfig.DISCOVER_MAINNET_URL
import com.algorand.android.BuildConfig.DISCOVER_TESTNET_URL
import com.algorand.android.usecase.GetIsActiveNodeTestnetUseCase
import com.algorand.common.remoteconfig.domain.usecase.IMMERSVE_BUTTON_TOGGLE
import com.algorand.common.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.common.remoteconfig.domain.usecase.STAKING_BUTTON_TOGGLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CoreActionsTabBarViewModel @Inject constructor(
    private val getIsActiveNodeTestnetUseCase: GetIsActiveNodeTestnetUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
) : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Idle)
    val viewState get() = _viewState.asStateFlow()

    fun initViewState() {
        val isImmersveToggleEnabled = isFeatureToggleEnabled(IMMERSVE_BUTTON_TOGGLE) && !isConnectedToTestnet()
        val isStakingToggleEnabled = isFeatureToggleEnabled(STAKING_BUTTON_TOGGLE)
        _viewState.value = ViewState.Content(isImmersveToggleEnabled, isStakingToggleEnabled)
    }

    fun getDiscoverBrowseDappUrl(): String {
        return if (isConnectedToTestnet())
            DISCOVER_BROWSE_DAPP_TESTNET_URL
        else
            DISCOVER_BROWSE_DAPP_MAINNET_URL
    }

    fun getDiscoverUrlWithPath(path: String): String {
        val baseDiscoverUrl = if (isConnectedToTestnet()) {
            DISCOVER_TESTNET_URL
        } else {
            DISCOVER_MAINNET_URL
        }
        return "$baseDiscoverUrl/$path"
    }

    fun isConnectedToTestnet(): Boolean {
        return getIsActiveNodeTestnetUseCase.invoke()
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val isImmersveEnabled: Boolean,
            val isStakingEnabled: Boolean
        ) : ViewState
    }
}
