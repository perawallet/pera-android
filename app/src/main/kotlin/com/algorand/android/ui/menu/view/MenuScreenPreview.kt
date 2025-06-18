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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel
import com.algorand.wallet.viewmodel.StateDelegate

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
    }
    val nftState = StateDelegate<MenuNftViewModel.ViewState>().apply {
        setDefaultState(MenuNftViewModel.ViewState.Idle)
    }
    val nftViewModel = MenuNftViewModel({ listOf() }, nftState)
    MenuScreen(nftViewModel, listener)
}
