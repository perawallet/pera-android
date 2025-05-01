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

package com.algorand.android.ui.rekeyedaccounts.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.algorand.android.R
import com.algorand.android.databinding.ItemRekeyedAccountSelectionAccountBinding
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AccountItem
import com.algorand.android.utils.AccountIconDrawable

class RekeyedAccountSelectionAccountViewHolder(
    private val binding: ItemRekeyedAccountSelectionAccountBinding,
    private val listener: Listener
) : ViewHolder(binding.root) {

    fun bind(item: AccountItem) {
        val accountAddress = item.accountDisplayName.accountAddress
        binding.rekeyedAccountSelectionStatefulAccountView.apply {
            val accountIconDrawable = AccountIconDrawable.create(
                context = context,
                sizeResId = R.dimen.spacing_xxxxlarge,
                accountIconDrawablePreview = item.accountIconDrawablePreview
            )
            setStartIconDrawable(accountIconDrawable)
            setTitleText(item.accountDisplayName.primaryDisplayName)
            setDescriptionText(item.accountDisplayName.secondaryDisplayName)
            setEndIconResource(R.drawable.ic_info)
            setEndIconClickListener { listener.onAccountItemInformationClick(accountAddress) }
        }
        binding.root.setOnClickListener { listener.onAccountItemClick(item) }
        binding.selectionIndicatorButton.apply {
            isSelected = item.isSelected
            setIconResource(item.selectorDrawableRes)
            setOnClickListener { listener.onAccountItemClick(item) }
        }
        binding.parentLayout.apply {
            isSelected = item.isSelected
            setBackgroundResource(R.drawable.bg_selector_found_account)
        }
    }

    interface Listener {
        fun onAccountItemClick(accountItem: AccountItem)
        fun onAccountItemInformationClick(accountAddress: String)
    }

    companion object {
        fun create(parent: ViewGroup, listener: Listener): RekeyedAccountSelectionAccountViewHolder {
            val binding = ItemRekeyedAccountSelectionAccountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return RekeyedAccountSelectionAccountViewHolder(binding, listener)
        }
    }
}
