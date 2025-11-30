@file:Suppress("EmptyFunctionBlock")
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

package com.algorand.android.ui.menu.view.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.ui.menu.view.MenuScreen
import com.algorand.android.ui.menu.view.MenuScreenListener
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@PreviewLightDark
@Composable
fun MenuScreenPreview() {
    val listener = object : MenuScreenListener {
        override fun onSettingsClick() {}
        override fun onScanQrClick() {}
        override fun onBuyAlgoClick() {}
        override fun onInviteFriendsClick() {}
        override fun onReceiveClick() {}
        override fun onNftClick() {}
        override fun onBuyGiftCardClick() {}
        override fun onGoToCardsClick() {}
        override fun onCreateCardClick() {}
        override fun onStakeClick() {}
    }
    MenuScreen(isXoSwapEnabled = true, getNftViewModel(), getCardViewModel(), listener)
}

private fun getNftViewModel(): MenuNftViewModel {
    return object : MenuNftViewModel {
        override fun initNftState() {}
        override val state: StateFlow<MenuNftViewModel.ViewState>
            get() = MutableStateFlow(MenuNftViewModel.ViewState.Idle)
    }
}

private fun getCardViewModel(): MenuCardsViewModel {
    return object : MenuCardsViewModel {
        override fun initCardState() {}
        override val state: StateFlow<MenuCardsViewModel.ViewState>
            get() = MutableStateFlow(MenuCardsViewModel.ViewState.NewUser)
    }
}
