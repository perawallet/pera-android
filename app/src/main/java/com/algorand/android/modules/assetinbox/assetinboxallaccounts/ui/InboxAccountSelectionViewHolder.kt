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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.R
import com.algorand.android.databinding.ItemInboxAccountBinding
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccountsWithAccount
import com.algorand.android.utils.AccountIconDrawable

class InboxAccountSelectionViewHolder(
    private val binding: ItemInboxAccountBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(assetInboxAllAccountsWithAccount: AssetInboxAllAccountsWithAccount) {
        with(binding) {
            with(assetInboxAllAccountsWithAccount) {
                accountNameTextView.text = accountDisplayName.getAccountPrimaryDisplayName()
                incomingAssetCountTextView.text = incomingAssetCountTextView.resources.getQuantityString(
                    R.plurals.incoming_assets,
                    requestCount,
                    requestCount
                )
                val accountIconDrawable = AccountIconDrawable.create(
                    context = accountIconImageView.context,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    sizeResId = R.dimen.spacing_xxxxlarge
                )
                accountIconImageView.setImageDrawable(accountIconDrawable)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): InboxAccountSelectionViewHolder {
            val binding = ItemInboxAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return InboxAccountSelectionViewHolder(binding)
        }
    }
}
