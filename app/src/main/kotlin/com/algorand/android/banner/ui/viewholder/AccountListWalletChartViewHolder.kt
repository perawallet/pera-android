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

package com.algorand.android.banner.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.algorand.android.databinding.ItemWalletChartBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.ui.accounts.model.AccountsLineChartData
import com.algorand.android.ui.accounts.view.AccountsLineChart
import com.algorand.android.ui.accounts.viewmodel.AccountsLineChartViewModel

class AccountListWalletChartViewHolder(
    binding: ItemWalletChartBinding
) : BaseViewHolder<BaseAccountListItem>(binding.root) {

    override fun bind(item: BaseAccountListItem) {
        if (item !is BaseAccountListItem.WalletChartItem) return
    }

    companion object {
        fun create(
            parent: ViewGroup,
            viewModel: AccountsLineChartViewModel,
            listener: AccountListWalletChartViewHolderListener
        ): AccountListWalletChartViewHolder {
            val binding = ItemWalletChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.root.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    AccountsLineChart(viewModel, listener::onItemSelected, listener::onItemDeselected)
                }
            }
            return AccountListWalletChartViewHolder(binding)
        }
    }

    interface AccountListWalletChartViewHolderListener {
        fun onItemSelected(chartData: AccountsLineChartData)
        fun onItemDeselected()
    }
}
