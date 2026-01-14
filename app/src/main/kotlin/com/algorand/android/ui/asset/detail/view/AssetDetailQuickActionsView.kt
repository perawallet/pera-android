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

package com.algorand.android.ui.asset.detail.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.AbstractComposeView
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.BuyAlgoButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.ReceiveButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SendButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.StakeButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SwapButton
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.quickaction.BuySellQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.QuickActionButtonContainer
import com.algorand.android.ui.compose.widget.quickaction.ReceiveQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SendQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.StakeQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SwapQuickActionButton

class AssetDetailQuickActionsView(context: Context, attrs: AttributeSet?) : AbstractComposeView(context, attrs) {

    private val quickActionItems = mutableStateListOf<AssetDetailQuickActionItem>()

    private var listener: AssetDetailQuickActionsViewListener? = null

    @Composable
    override fun Content() {
        PeraTheme {
            QuickActionButtonContainer {
                quickActionItems.forEach {
                    when (it) {
                        is SwapButton -> SwapQuickActionButton { listener?.onSwapClick() }
                        BuyAlgoButton -> BuySellQuickActionButton { listener?.onBuyAlgoClick() }
                        ReceiveButton -> ReceiveQuickActionButton { listener?.onReceiveClick() }
                        SendButton -> SendQuickActionButton { listener?.onSendClick() }
                        StakeButton -> StakeQuickActionButton { listener?.onStakeClick() }
                    }
                }
            }
        }
    }

    fun setListener(listener: AssetDetailQuickActionsViewListener) {
        this.listener = listener
    }

    fun setQuickActionItems(items: List<AssetDetailQuickActionItem>) {
        quickActionItems.clear()
        quickActionItems.addAll(items)
    }

    interface AssetDetailQuickActionsViewListener {
        fun onSwapClick()
        fun onBuyAlgoClick()
        fun onSendClick()
        fun onReceiveClick()
        fun onStakeClick()
    }
}
