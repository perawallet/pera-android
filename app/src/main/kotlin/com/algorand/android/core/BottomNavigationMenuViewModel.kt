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

package com.algorand.android.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.core.BottomNavigationMenuViewModel.ViewEvent
import com.algorand.android.core.BottomNavigationMenuViewModel.ViewState
import com.algorand.android.core.bottomnav.model.BottomNavMenuItem
import com.algorand.android.utils.isStagingApp
import com.algorand.wallet.node.domain.usecase.IsSelectedNodeTestnet
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BottomNavigationMenuViewModel @Inject constructor(
    private val isSelectedNodeTestnet: IsSelectedNodeTestnet,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initBottomNavState() {
        stateDelegate.onState<ViewState.Idle> {
            viewModelScope.launch {
                val menuItems = getMenuItems()
                stateDelegate.updateState { ViewState.Content(menuItems) }
            }
        }
    }

    fun updateBottomNavOnNodeChange() {
        viewModelScope.launch {
            eventDelegate.sendEvent(ViewEvent.SetItemEnabled(R.id.discoverHomeNavigation, isDiscoverEnabled()))
        }
    }

    private suspend fun getMenuItems(): List<BottomNavMenuItem> {
        return buildList {
            add(getHomeItem())
            add(getDiscoverItem())
            add(getStakingItem())
            add(getNftsItem())
            add(getMenuItem())
        }
    }

    private suspend fun isDiscoverEnabled(): Boolean = isSelectedNodeTestnet().not() || isStagingApp()

    private fun getHomeItem(): BottomNavMenuItem {
        return BottomNavMenuItem(
            id = R.id.accountsFragment,
            titleResId = R.string.home,
            iconResId = R.drawable.ic_home,
            enabled = true
        )
    }

    private suspend fun getDiscoverItem(): BottomNavMenuItem {
        return BottomNavMenuItem(
            id = R.id.discoverHomeNavigation,
            titleResId = R.string.discover,
            iconResId = R.drawable.ic_global,
            enabled = isDiscoverEnabled()
        )
    }

    private fun getSwapItem(): BottomNavMenuItem {
        return BottomNavMenuItem(
            id = R.id.swapFragment,
            titleResId = R.string.swap,
            iconResId = R.drawable.ic_swap,
            enabled = false
        )
    }

    private fun getStakingItem(): BottomNavMenuItem {
        return BottomNavMenuItem(
            id = R.id.stakingFragment,
            titleResId = R.string.staking,
            iconResId = R.drawable.ic_staking,
            enabled = true
        )
    }

    private fun getNftsItem(): BottomNavMenuItem {
        return BottomNavMenuItem(
            id = R.id.collectiblesFragment,
            titleResId = R.string.nfts,
            iconResId = R.drawable.ic_collectibles,
            enabled = true
        )
    }

    private fun getMenuItem(): BottomNavMenuItem {
        return BottomNavMenuItem(
            id = R.id.menuFragment,
            titleResId = R.string.menu,
            iconResId = R.drawable.ic_menu,
            enabled = true
        )
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val menuItems: List<BottomNavMenuItem>) : ViewState
    }

    sealed interface ViewEvent {
        data class SetItemEnabled(val itemId: Int, val enabled: Boolean) : ViewEvent
    }
}
