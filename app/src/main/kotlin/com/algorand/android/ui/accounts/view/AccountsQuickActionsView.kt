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
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.quickaction.BuySellQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.FundQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.QuickActionButtonContainer
import com.algorand.android.ui.compose.widget.quickaction.SendQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.StakeQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SwapQuickActionButton

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
                        SwapQuickActionButton { listener?.onSwapClick() }
                        if (state.isXoSwapEnabled) {
                            FundQuickActionButton { listener?.onFundClick() }
                        } else {
                            BuySellQuickActionButton { listener?.onBuySellClick() }
                        }
                        if (state.isStakingEnabled) {
                            StakeQuickActionButton { listener?.onStakingClick() }
                        }
                        SendQuickActionButton { listener?.onSendClick() }
                    }
                }
            }
        }
    }

    fun setListener(listener: AccountsQuickActionsListener) {
        this.listener = listener
    }

    fun init(isStakingEnabled: Boolean, isXoSwapEnabled: Boolean) {
        viewState = ViewState.Content(isStakingEnabled, isXoSwapEnabled)
    }

    interface AccountsQuickActionsListener {
        fun onBuySellClick()
        fun onSendClick()
        fun onSwapClick()
        fun onStakingClick()
        fun onFundClick()
    }

    private sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val isStakingEnabled: Boolean,
            val isXoSwapEnabled: Boolean
        ) : ViewState
    }
}
