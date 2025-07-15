/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.accountdetail.assets.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.R
import com.algorand.android.databinding.ItemAccountValueBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accountdetail.assets.ui.AccountAssetsLineChartViewModel
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.AccountPortfolioItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AddressLineChartData
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.view.StatefulPeraLineChart
import com.algorand.android.ui.compose.widget.chart.view.StatefulPeraLineChartListener
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.formatDateToChartDateString

class AccountValueViewHolder(
    private val binding: ItemAccountValueBinding,
    private val address: String,
    private val viewModel: AccountAssetsLineChartViewModel,
    private val listener: Listener,
) : BaseViewHolder<AccountDetailAccountsItem>(binding.root) {

    override fun bind(item: AccountDetailAccountsItem) {
        if (item !is AccountPortfolioItem) return
        setPrimaryText(item.accountPrimaryFormattedParityValue)
        setSecondaryText(item.accountSecondaryFormattedParityValue)
        setMinRequiredBalanceText(item.requiredMinBalance)
        setChart(item)
        viewModel.init(address)
    }

    private fun getChartListener(item: AccountPortfolioItem): StatefulPeraLineChartListener {
        return object : StatefulPeraLineChartListener {
            override fun onItemSelected(item: PeraLineChartData) {
                val accountAssetsData = item as? AddressLineChartData ?: return
                setPrimaryText(accountAssetsData.primaryAmountRenderer.getDisplayValue())
                setSecondaryText(accountAssetsData.primaryAmountRenderer.getDisplayValue())
                binding.helperTextView.text = formatDateToChartDateString(accountAssetsData.datetime)
            }

            override fun onItemDeselected() {
                setPrimaryText(item.accountPrimaryFormattedParityValue)
                setSecondaryText(item.accountSecondaryFormattedParityValue)
                setMinRequiredBalanceText(item.requiredMinBalance)
            }

            override fun onChartTap() {
                listener.onChartTap()
            }
        }
    }

    private fun setMinRequiredBalanceText(requiredMinBalance: String) {
        binding.helperTextView.apply {
            text = binding.root.resources.getString(R.string.min_balance, requiredMinBalance)
            setOnClickListener { listener.onInfoButtonClick() }
        }
    }

    private fun setPrimaryText(primaryParityValue: String?) {
        binding.primaryValueTextView.apply {
            text = primaryParityValue.orEmpty()
            setOnClickListener { listener.onAccountValueClick() }
        }
    }

    private fun setSecondaryText(secondaryParityValue: String?) {
        binding.secondaryValueTextView.apply {
            text = secondaryParityValue.orEmpty()
            setOnClickListener { listener.onAccountValueClick() }
        }
    }

    private fun setChart(item: AccountPortfolioItem) {
        with(binding.chartComposeView) {
            if (item.displayChart) {
                setContent {
                    PeraTheme {
                        StatefulPeraLineChart(viewModel, getChartListener(item))
                    }
                }
                show()
            } else {
                hide()
                binding.chartComposeView.removeAllViews()
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            address: String,
            viewModel: AccountAssetsLineChartViewModel,
            listener: Listener
        ): AccountValueViewHolder {
            val binding = ItemAccountValueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountValueViewHolder(binding, address, viewModel, listener)
        }
    }

    interface Listener {
        fun onAccountValueClick()
        fun onInfoButtonClick()
        fun onChartTap()
    }
}
