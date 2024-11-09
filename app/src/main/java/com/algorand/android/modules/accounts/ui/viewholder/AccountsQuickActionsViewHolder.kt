/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accounts.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout.GONE
import androidx.constraintlayout.motion.widget.MotionLayout.VISIBLE
import com.algorand.android.databinding.ItemAccountsQuickActionsBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accounts.domain.model.BaseAccountListItem
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class AccountsQuickActionsViewHolder(
    private val binding: ItemAccountsQuickActionsBinding,
    private val listener: AccountsQuickActionsListener
) : BaseViewHolder<BaseAccountListItem>(binding.root) {

    override fun bind(item: BaseAccountListItem) {
        if (item !is BaseAccountListItem.QuickActionsItem) return
        with(binding) {
            stakingButton.setOnClickListener { listener.onStakingClick() }
            buySellButton.setOnClickListener { listener.onBuySellClick() }
            sendButton.setOnClickListener { listener.onSendClick() }
            swapButton.apply {
                isSelected = item.isSwapButtonSelected
                setOnClickListener { listener.onSwapClick() }
            }
            scanQrButton.setOnClickListener { listener.onScanQrClick() }
        }
    }

    interface AccountsQuickActionsListener {
        fun onBuySellClick()
        fun onSendClick()
        fun onSwapClick()
        fun onScanQrClick()
        fun onStakingClick()
    }

    companion object {
        fun create(parent: ViewGroup, listener: AccountsQuickActionsListener): AccountsQuickActionsViewHolder {
            val binding = ItemAccountsQuickActionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            handleNavigationButtonsForFeatureFlags(binding)
            return AccountsQuickActionsViewHolder(binding, listener)
        }

        fun handleNavigationButtonsForFeatureFlags(binding: ItemAccountsQuickActionsBinding) {
            val enableStaking = FirebaseRemoteConfig.getInstance().getBoolean("enable_staking")
            if (enableStaking) {
                binding.stakingButton.visibility = VISIBLE
                binding.buySellButton.visibility = GONE
            } else {
                binding.stakingButton.visibility = GONE
                binding.buySellButton.visibility = VISIBLE
            }
        }
    }
}
