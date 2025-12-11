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

package com.algorand.android.ui.compose.widget.quickaction

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.algorand.android.R

@Composable
fun SwapQuickActionButton(onClick: () -> Unit) {
    PrimaryQuickActionButton(
        iconResId = R.drawable.ic_swap,
        text = stringResource(R.string.swap),
        onClick = onClick
    )
}

@Composable
fun BuySellQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_buy_sell_small,
        text = stringResource(R.string.buy_sell),
        onClick = onClick
    )
}

@Composable
fun StakeQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_staking,
        text = stringResource(R.string.staking),
        onClick = onClick
    )
}

@Composable
fun SendQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_send,
        text = stringResource(R.string.send),
        onClick = onClick
    )
}

@Composable
fun AssetInboxQuickActionButton(isSelected: Boolean, onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_asset_inbox_quick_action,
        text = stringResource(R.string.asset_inbox),
        showIndicator = isSelected,
        onClick = onClick
    )
}

@Composable
fun CopyAddressQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_copy,
        text = stringResource(R.string.copy_address),
        onClick = onClick
    )
}

@Composable
fun MoreQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_more_bold,
        text = stringResource(R.string.more),
        onClick = onClick
    )
}

@Composable
fun ShowAddressQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_qr,
        text = stringResource(R.string.show_address),
        onClick = onClick
    )
}

@Composable
fun ReceiveQuickActionButton(onClick: () -> Unit) {
    SecondaryQuickActionButton(
        iconResId = R.drawable.ic_receive,
        text = stringResource(R.string.receive),
        onClick = onClick
    )
}
