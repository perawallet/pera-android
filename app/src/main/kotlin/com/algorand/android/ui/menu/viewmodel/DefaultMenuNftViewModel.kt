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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DefaultMenuNftViewModel @Inject constructor(
    private val getRecentlyAddedCollectibleUrls: GetRecentlyAddedCollectibleUrls,
    private val stateDelegate: StateDelegate<ViewState>
) : MenuNftViewModel, ViewModel() {

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override fun initNftState() {
        viewModelScope.launch {
            val urls = getRecentlyAddedCollectibleUrls(COLLECTIBLE_PREVIEW_COUNT)
            stateDelegate.updateState { ViewState.Content(urls) }
        }
    }

    private companion object {
        const val COLLECTIBLE_PREVIEW_COUNT = 3
    }
}
