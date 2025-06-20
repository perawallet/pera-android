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

package com.algorand.android.ui.menu.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.menu.viewmodel.DefaultMenuCardsViewModel
import com.algorand.android.ui.menu.viewmodel.DefaultMenuNftViewModel
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegation
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegationImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment(0), MenuScreenListener,
    BottomNavBarFragmentDelegation by BottomNavBarFragmentDelegationImpl() {

    override val fragmentConfiguration = FragmentConfiguration(isBottomBarNeeded = true)

    private val menuNftViewModel: DefaultMenuNftViewModel by viewModels()

    private val cardsViewModel: DefaultMenuCardsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            MenuScreen(menuNftViewModel, cardsViewModel, this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBottomNavBarFragmentDelegation(this)
    }

    override fun onSettingsClick() {
        nav(MenuFragmentDirections.actionMenuFragmentToSettingsFragment())
    }

    override fun onScanQrClick() {
        nav(HomeNavigationDirections.actionGlobalAccountsQrScannerFragment())
    }

    override fun onBuyAlgoClick() {
        nav(HomeNavigationDirections.actionGlobalBuySellActionsBottomSheet())
    }

    override fun onInviteFriendsClick() {
        nav(MenuFragmentDirections.actionMenuFragmentToInviteFriendsBottomSheet())
    }

    override fun onReceiveClick() {
        nav(HomeNavigationDirections.actionGlobalReceiveAccountSelectionFragment())
    }

    override fun onNftClick() {
        nav(MenuFragmentDirections.actionMenuFragmentToCollectiblesFragment(registerBottomNavDelegation = false))
    }

    override fun onCreateCardClick() {
        nav(HomeNavigationDirections.actionGlobalCardsFragment())
    }

    override fun onGoToCardsClick() {
        nav(HomeNavigationDirections.actionGlobalCardsFragment())
    }
}
