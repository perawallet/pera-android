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

package com.algorand.android.ui.menu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel.ViewState
import com.algorand.wallet.asset.domain.usecase.GetRecentlyAddedCollectibleUrls
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MenuNftViewModel @Inject constructor(
    private val getRecentlyAddedCollectibleUrls: GetRecentlyAddedCollectibleUrls,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun init() {
        stateDelegate.onState<ViewState.Idle> {
            viewModelScope.launch {
                val urls = getRecentlyAddedCollectibleUrls(COLLECTIBLE_PREVIEW_COUNT)
                stateDelegate.updateState { ViewState.Content(urls) }
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val urls: List<String>) : ViewState
    }

    private companion object {
        const val COLLECTIBLE_PREVIEW_COUNT = 3
    }
}
