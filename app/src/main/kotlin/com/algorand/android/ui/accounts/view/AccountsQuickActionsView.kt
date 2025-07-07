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

package com.algorand.android.ui.accounts.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.stringResource
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.quickaction.PrimaryQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.QuickActionButtonContainer
import com.algorand.android.ui.compose.widget.quickaction.SecondaryQuickActionButton

class AccountsQuickActionsView(context: Context, attrs: AttributeSet? = null) : AbstractComposeView(context, attrs) {

    private var listener: AccountsQuickActionsListener? = null

    private var viewState by mutableStateOf<ViewState>(ViewState.Idle)

    @Composable
    override fun Content() {
        PeraTheme {
            QuickActionButtonContainer {
                when (val state = viewState) {
                    is ViewState.Idle -> Unit
                    is ViewState.Content -> {
                        SwapButton(state.isSwapButtonSelected)
                        BuySellButton()
                        if (state.isStakingEnabled) {
                            StakeButton()
                        }
                        SendButton()
                    }
                }
            }
        }
    }

    fun setListener(listener: AccountsQuickActionsListener) {
        this.listener = listener
    }

    fun init(
        isStakingEnabled: Boolean,
        isSwapButtonSelected: Boolean
    ) {
        viewState = ViewState.Content(
            isStakingEnabled = isStakingEnabled,
            isSwapButtonSelected = isSwapButtonSelected
        )
    }

    @Composable
    private fun SwapButton(isSelected: Boolean) {
        PrimaryQuickActionButton(
            iconResId = R.drawable.ic_swap,
            text = stringResource(R.string.swap),
            showIndicator = isSelected,
            onClick = { listener?.onSwapClick() }
        )
    }

    @Composable
    private fun BuySellButton() {
        SecondaryQuickActionButton(
            iconResId = R.drawable.ic_buy_sell_small,
            text = stringResource(R.string.buy_sell),
            onClick = { listener?.onBuySellClick() },
        )
    }

    @Composable
    private fun StakeButton() {
        SecondaryQuickActionButton(
            iconResId = R.drawable.ic_staking,
            text = stringResource(R.string.staking),
            onClick = { listener?.onStakingClick() },
        )
    }

    @Composable
    private fun SendButton() {
        SecondaryQuickActionButton(
            iconResId = R.drawable.ic_send,
            text = stringResource(R.string.send),
            onClick = { listener?.onSendClick() },
        )
    }

    interface AccountsQuickActionsListener {
        fun onBuySellClick()
        fun onSendClick()
        fun onSwapClick()
        fun onStakingClick()
    }

    private sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val isStakingEnabled: Boolean, val isSwapButtonSelected: Boolean) : ViewState
    }
}

