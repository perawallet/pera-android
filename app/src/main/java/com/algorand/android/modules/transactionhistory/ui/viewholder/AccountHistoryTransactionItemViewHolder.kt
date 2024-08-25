/*
 * Copyright 2022 Pera Wallet, LDA
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

package com.algorand.android.modules.transactionhistory.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.databinding.ItemAccountHistoryTransactionBinding
import com.algorand.android.transactionhistoryui.model.BaseTransactionItem
import com.algorand.android.utils.extensions.setTextAndVisibility

class AccountHistoryTransactionItemViewHolder(
    private val binding: ItemAccountHistoryTransactionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(transaction: BaseTransactionItem.TransactionItem) {
        with(binding) {
            with(transaction) {
                pendingTransactionProgressBar.isVisible = isPending
                nameRes?.let { titleTextView.setText(it) }
                descriptionTextView.setTextAndVisibility(description)

                if (isAmountVisible) {
                    amountTextView.setAmount(formattedAmount, amountColorRes)
                }
                amountTextView.isVisible = isAmountVisible
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): AccountHistoryTransactionItemViewHolder {
            val binding = ItemAccountHistoryTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return AccountHistoryTransactionItemViewHolder(binding)
        }
    }
}
