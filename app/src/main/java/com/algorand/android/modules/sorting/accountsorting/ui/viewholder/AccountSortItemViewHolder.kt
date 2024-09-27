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

package com.algorand.android.modules.sorting.accountsorting.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.R
import com.algorand.android.databinding.ItemAccountSortBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.sorting.accountsorting.domain.model.BaseAccountSortingListItem
import com.algorand.android.utils.AccountIconDrawable

class AccountSortItemViewHolder(
    private val binding: ItemAccountSortBinding,
    private val listener: DragButtonPressedListener
) : BaseViewHolder<BaseAccountSortingListItem>(binding.root) {

    override fun bind(item: BaseAccountSortingListItem) {
        if (item !is BaseAccountSortingListItem.AccountSortListItem) return
        with(binding) {
            with(item.accountListItem.itemConfiguration) {
                setAccountStartIconDrawable(accountIconDrawablePreview)
                setAccountTitleText(accountDisplayName?.primaryDisplayName)
                setAccountDescriptionText(accountDisplayName?.secondaryDisplayName)
                setAccountItemDragButton()
            }
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

    private fun setAccountItemDragButton() {
        binding.accountItemView.apply {
            setButtonState(AccountAssetItemButtonState.DRAGGABLE)
            setActionButtonOnTouchClickListener { listener.onPressed(this@AccountSortItemViewHolder) }
        }
    }

    fun interface DragButtonPressedListener {
        fun onPressed(viewHolder: AccountSortItemViewHolder)
    }

    companion object {
        fun create(parent: ViewGroup, listener: DragButtonPressedListener): AccountSortItemViewHolder {
            val binding = ItemAccountSortBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountSortItemViewHolder(binding, listener)
        }
    }
}
