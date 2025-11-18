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

package com.algorand.android.modules.accountdetail.assets.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.databinding.ItemAccountDetailQuickActionsBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.ui.accountdetail.assets.view.AccountDetailQuickActionsView

class AccountDetailQuickActionsViewHolder(
    private val binding: ItemAccountDetailQuickActionsBinding,
    private val listener: AccountDetailQuickActionsListener
) : BaseViewHolder<AccountDetailAccountsItem>(binding.root) {

    override fun bind(item: AccountDetailAccountsItem) {
        if (item !is AccountDetailAccountsItem.QuickActionItemContainer) return
        binding.root.apply {
            setQuickActions(item.quickActionItems)
            setListener(listener)
        }
    }

    interface AccountDetailQuickActionsListener : AccountDetailQuickActionsView.AccountDetailQuickActionsViewListener

    companion object {
        fun create(
            parent: ViewGroup,
            listener: AccountDetailQuickActionsListener
        ): AccountDetailQuickActionsViewHolder {
            val binding = ItemAccountDetailQuickActionsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountDetailQuickActionsViewHolder(binding, listener)
        }
    }
}
