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

package com.algorand.android.modules.swap.transactionsummary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.R
import com.algorand.android.databinding.ItemSwapAccountBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.swapui.txnsummary.model.BaseSwapTransactionSummaryItem
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.setDrawable

class SwapAccountItemViewHolder(
    private val binding: ItemSwapAccountBinding,
    private val listener: SwapAccountItemListener
) : BaseViewHolder<BaseSwapTransactionSummaryItem>(binding.root) {

    override fun bind(item: BaseSwapTransactionSummaryItem) {
        if (item !is BaseSwapTransactionSummaryItem.SwapAccountItemTransaction) return
        with(item) {
            binding.accountTextView.apply {
                text = accountDisplayName.primaryDisplayName
                val accountIconDrawable = AccountIconDrawable.create(
                    context = context,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    sizeResId = R.dimen.spacing_xlarge
                )
                setDrawable(start = accountIconDrawable)
                setOnLongClickListener {
                    listener.onAccountNameLongClick(accountDisplayName.accountAddress); true
                }
            }
        }
    }

    fun interface SwapAccountItemListener {
        fun onAccountNameLongClick(accountAddress: String)
    }

    companion object {
        fun create(parent: ViewGroup, listener: SwapAccountItemListener): SwapAccountItemViewHolder {
            val binding = ItemSwapAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SwapAccountItemViewHolder(binding, listener)
        }
    }
}
