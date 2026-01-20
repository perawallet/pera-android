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

package com.algorand.android.ui.accountdetail.assets.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.AbstractComposeView
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.AssetInbox
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.BuyAlgoButton
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.CopyAddressButton
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.FundButton
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.MoreButton
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.SendButton
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.ShowAddressButton
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailQuickActionItem.SwapButton
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.quickaction.BuySellQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.CopyAddressQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.FundQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.InboxQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.MoreQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.QuickActionButtonContainer
import com.algorand.android.ui.compose.widget.quickaction.SendQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.ShowAddressQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SwapQuickActionButton

class AccountDetailQuickActionsView(context: Context, attrs: AttributeSet?) : AbstractComposeView(context, attrs) {

    private var quickActions = mutableStateListOf<AccountDetailQuickActionItem>()

    private var listener: AccountDetailQuickActionsViewListener? = null

    @Composable
    override fun Content() {
        PeraTheme {
            QuickActionButtonContainer {
                quickActions.forEach {
                    when (it) {
                        BuyAlgoButton -> BuySellQuickActionButton { listener?.onBuySellClick() }
                        FundButton -> FundQuickActionButton { listener?.onFundClick() }
                        CopyAddressButton -> CopyAddressQuickActionButton { listener?.onCopyAddressClick() }
                        MoreButton -> MoreQuickActionButton { listener?.onMoreClick() }
                        SendButton -> SendQuickActionButton { listener?.onSendClick() }
                        ShowAddressButton -> ShowAddressQuickActionButton { listener?.onShowAddressClick() }
                        is AssetInbox -> InboxQuickActionButton(it.isSelected) { listener?.onInboxClick() }
                        is SwapButton -> SwapQuickActionButton { listener?.onSwapClick() }
                    }
                }
            }
        }
    }

    fun setQuickActions(actions: List<AccountDetailQuickActionItem>) {
        quickActions.clear()
        quickActions.addAll(actions)
    }

    fun setListener(listener: AccountDetailQuickActionsViewListener) {
        this.listener = listener
    }

    interface AccountDetailQuickActionsViewListener {
        fun onInboxClick()
        fun onSendClick()
        fun onSwapClick()
        fun onMoreClick()
        fun onCopyAddressClick()
        fun onShowAddressClick()
        fun onBuySellClick()
        fun onFundClick()
    }
}
