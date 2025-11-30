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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel

@Composable
fun MenuScreen(
    isXoSwapEnabled: Boolean,
    isStakeEnabled: Boolean,
    menuNftViewModel: MenuNftViewModel,
    menuCardViewModel: MenuCardsViewModel,
    listener: MenuScreenListener
) {
    Column(
        modifier = Modifier
            .background(color = PeraTheme.colors.background.primary)
            .fillMaxSize()
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
    ) {
        MenuToolbar(listener::onSettingsClick, listener::onScanQrClick)
        MenuItemSpacer()
        MenuListCardItem(menuCardViewModel, listener)
        MenuItemSpacer()
        MenuListNftItem(menuNftViewModel, listener::onNftClick)
        MenuItemSpacer()
        if (isXoSwapEnabled) {
            MenuListBuyGiftCardItem(listener::onBuyGiftCardClick)
            if (isStakeEnabled) {
                MenuItemSpacer()
                MenuListStakeItem(listener::onStakeClick)
            }
        } else {
            MenuListBuyAlgoItem(listener::onBuyAlgoClick)
        }
        MenuItemSpacer()
        MenuListReceiveItem(listener::onReceiveClick)
        MenuItemSpacer()
        MenuListInviteFriendsItem(listener::onInviteFriendsClick)
    }
}

@Composable
private fun MenuToolbar(onSettingsClick: () -> Unit, onScanQrClick: () -> Unit) {
    PeraToolbar(
        text = stringResource(R.string.menu),
        endContainer = {
            PeraToolbarIcon(
                modifier = Modifier.clickable { onScanQrClick() },
                iconResId = R.drawable.ic_qr_scan
            )
            Spacer(modifier = Modifier.width(8.dp))
            PeraToolbarIcon(
                modifier = Modifier.clickable { onSettingsClick() },
                iconResId = R.drawable.ic_settings
            )
        }
    )
}

@Composable
private fun MenuItemSpacer() {
    Spacer(modifier = Modifier.height(12.dp))
}

interface MenuScreenListener : MenuListCardItemListener {
    fun onSettingsClick()
    fun onScanQrClick()
    fun onBuyAlgoClick()
    fun onInviteFriendsClick()
    fun onReceiveClick()
    fun onNftClick()
    fun onBuyGiftCardClick()
    fun onStakeClick()
}
