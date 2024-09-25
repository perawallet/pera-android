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

package com.algorand.android.modules.swap.introduction.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.algorand.android.core.BaseViewModel
import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.AccountSelection
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Introduction
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Swap
import com.algorand.android.module.swap.component.common.usecase.GetSwapNavigationDestination
import com.algorand.android.module.swap.component.introduction.domain.usecase.SetSwapFeatureIntroductionPageVisibility
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SwapIntroductionViewModel @Inject constructor(
    private val setSwapFeatureIntroductionPageVisibility: SetSwapFeatureIntroductionPageVisibility,
    private val getSwapNavigationDestination: GetSwapNavigationDestination,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val navArgs = SwapIntroductionFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val accountAddress: String? = navArgs.accountAddress
    private val fromAssetId = navArgs.fromAssetId.takeIf { it != DEFAULT_ASSET_ID_ARG }
    private val toAssetId = navArgs.toAssetId.takeIf { it != DEFAULT_ASSET_ID_ARG }

    private val _swapNavDirectionFlow = MutableStateFlow<Event<NavDirections>?>(null)
    val swapNavDirectionFlow: StateFlow<Event<NavDirections>?>
        get() = _swapNavDirectionFlow

    init {
        setIntroductionPageAsShowed()
    }

    fun onStartSwappingClick() {
        viewModelScope.launch {
            val direction = getSwapNavDirection() ?: return@launch
            _swapNavDirectionFlow.value = Event(direction)
        }
    }

    private suspend fun getSwapNavDirection(): NavDirections? {
        return when (val swapNavigationDestination = getSwapNavigationDestination(accountAddress)) {
            AccountSelection -> getAccountSelectionNavigationDirection()
            is Swap -> getSwapNavigationDirection(swapNavigationDestination.address)
            Introduction -> null
        }
    }

    private fun getAccountSelectionNavigationDirection(): NavDirections {
        return SwapIntroductionFragmentDirections.actionSwapIntroductionFragmentToSwapAccountSelectionNavigation(
            fromAssetId = fromAssetId ?: DEFAULT_ASSET_ID_ARG,
            toAssetId = toAssetId ?: DEFAULT_ASSET_ID_ARG
        )
    }

    private fun getSwapNavigationDirection(address: String): NavDirections {
        return SwapIntroductionFragmentDirections.actionSwapIntroductionFragmentToSwapNavigation(
            accountAddress = address,
            fromAssetId = fromAssetId ?: DEFAULT_ASSET_ID_ARG,
            toAssetId = toAssetId ?: DEFAULT_ASSET_ID_ARG
        )
    }

    private fun setIntroductionPageAsShowed() {
        viewModelScope.launchIO {
            setSwapFeatureIntroductionPageVisibility(false)
        }
    }

    companion object {
        private const val DEFAULT_ASSET_ID_ARG = -1L
    }
}
