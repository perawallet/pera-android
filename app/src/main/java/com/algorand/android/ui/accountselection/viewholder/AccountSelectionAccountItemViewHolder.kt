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
 */

package com.algorand.android.ui.accountselection.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.R
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.databinding.ItemAccountSimpleBinding
import com.algorand.android.utils.AccountIconDrawable

class AccountSelectionAccountItemViewHolder(
    private val binding: ItemAccountSimpleBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BaseAccountSelectionListItem.BaseAccountItem.AccountItem) {
        with(item.accountListItem.itemConfiguration) {
            setAccountStartIconDrawable(accountIconDrawablePreview)
            setAccountTitleText(accountDisplayName?.primaryDisplayName)
            setAccountDescriptionText(accountDisplayName?.secondaryDisplayName)
            setAccountPrimaryValueText(primaryValueText)
            setAccountSecondaryValueText(secondaryValueText)
        }
    }

    private fun setAccountStartIconDrawable(accountIconDrawablePreview: AccountIconDrawablePreview?) {
        if (accountIconDrawablePreview != null) {
            with(binding.accountItemView) {
                val accountIconDrawable = AccountIconDrawable.create(
                    context = context,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    sizeResId = R.dimen.spacing_xxxxlarge
                )
                setStartIconDrawable(accountIconDrawable)
            }
        }
    }

    private fun setAccountTitleText(accountTitleText: String?) {
        binding.accountItemView.setTitleText(accountTitleText)
    }

    private fun setAccountDescriptionText(accountDescriptionText: String?) {
        binding.accountItemView.setDescriptionText(accountDescriptionText)
    }

    private fun setAccountPrimaryValueText(accountPrimaryValue: String?) {
        binding.accountItemView.setPrimaryValueText(accountPrimaryValue)
    }

    private fun setAccountSecondaryValueText(accountSecondaryValue: String?) {
        binding.accountItemView.setSecondaryValueText(accountSecondaryValue)
    }

    companion object {
        fun create(parent: ViewGroup): AccountSelectionAccountItemViewHolder {
            val binding = ItemAccountSimpleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountSelectionAccountItemViewHolder(binding)
        }
    }
}
