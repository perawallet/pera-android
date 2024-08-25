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

package com.algorand.android.ui.notificationfilter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.R
import com.algorand.android.databinding.ItemAccountNotificationFilterBinding
import com.algorand.android.utils.AccountIconDrawable

class AccountNotificationFilterViewHolder(
    private val binding: ItemAccountNotificationFilterBinding,
    private val accountOptionChanged: (String, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(accountNotificationOption: AccountNotificationOption) {
        with(accountNotificationOption) {
            with(binding) {
                nameTextView.text = accountItemConfiguration.accountDisplayName?.primaryDisplayName

                if (accountItemConfiguration.accountIconDrawablePreview != null) {
                    val accountIconDrawable = AccountIconDrawable.create(
                        context = binding.root.context,
                        accountIconDrawablePreview = accountItemConfiguration.accountIconDrawablePreview!!,
                        sizeResId = R.dimen.spacing_xxxxlarge
                    )
                    typeImageView.setImageDrawable(accountIconDrawable)
                }

                filterSwitch.setOnCheckedChangeListener(null)
                filterSwitch.isChecked = accountNotificationOption.isFiltered.not()
            }
            enableSwitchCheckedChangeListener(this)
        }
    }

    private fun enableSwitchCheckedChangeListener(
        accountNotificationOption: AccountNotificationOption
    ) {
        binding.filterSwitch.setOnCheckedChangeListener { _, isChecked ->
            accountNotificationOption.isFiltered = !isChecked
            accountOptionChanged.invoke(
                accountNotificationOption.accountItemConfiguration.accountAddress,
                !isChecked
            )
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            accountOptionChanged: (String, Boolean) -> Unit
        ): AccountNotificationFilterViewHolder {
            val binding =
                ItemAccountNotificationFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountNotificationFilterViewHolder(binding, accountOptionChanged)
        }
    }
}
