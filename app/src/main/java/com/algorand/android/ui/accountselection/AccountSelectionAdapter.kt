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

package com.algorand.android.ui.accountselection

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.R
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem.BaseAccountItem
import com.algorand.android.module.drawable.BaseDiffUtil
import com.algorand.android.ui.accountselection.viewholder.AccountSelectionAccountErrorItemViewHolder
import com.algorand.android.ui.accountselection.viewholder.AccountSelectionAccountItemViewHolder
import com.algorand.android.ui.accountselection.viewholder.AccountSelectionContactItemViewHolder
import com.algorand.android.ui.accountselection.viewholder.AccountSelectionHeaderItemViewHolder
import com.algorand.android.ui.accountselection.viewholder.AccountSelectionNftDomainAccountItemViewHolder
import com.algorand.android.ui.accountselection.viewholder.AccountSelectionPasteItemViewHolder

class AccountSelectionAdapter(
    private val listener: Listener,
) : ListAdapter<BaseAccountSelectionListItem, RecyclerView.ViewHolder>(BaseDiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BaseAccountSelectionListItem.PasteItem -> R.layout.item_paste_address
            is BaseAccountSelectionListItem.HeaderItem -> R.layout.item_header_simple
            is BaseAccountItem.ContactItem -> R.layout.item_contact_simple
            is BaseAccountItem.AccountItem -> R.layout.item_account_simple
            is BaseAccountItem.AccountErrorItem -> R.layout.item_account_error_simple
            is BaseAccountItem.NftDomainAccountItem -> R.layout.item_nft_domain_account
            else -> throw Exception("$logTag list item is unknown")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_paste_address -> createPasteItemViewHolder(parent)
            R.layout.item_header_simple -> createHeaderItemViewHolder(parent)
            R.layout.item_contact_simple -> createContactItemViewHolder(parent)
            R.layout.item_account_simple -> createAccountItemViewHolder(parent)
            R.layout.item_account_error_simple -> createAccountErrorItemViewHolder(parent)
            R.layout.item_nft_domain_account -> createNftDomainAccountItemViewHolder(parent)
            else -> throw Exception("$logTag list item is unknown")
        }
    }

    private fun createPasteItemViewHolder(parent: ViewGroup): AccountSelectionPasteItemViewHolder {
        return AccountSelectionPasteItemViewHolder.create(parent).apply {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    (getItem(bindingAdapterPosition) as BaseAccountSelectionListItem.PasteItem).run {
                        listener.onPasteItemClick(address)
                    }
                }
            }
        }
    }

    private fun createHeaderItemViewHolder(parent: ViewGroup): AccountSelectionHeaderItemViewHolder {
        return AccountSelectionHeaderItemViewHolder.create(parent)
    }

    private fun createContactItemViewHolder(parent: ViewGroup): AccountSelectionContactItemViewHolder {
        return AccountSelectionContactItemViewHolder.create(parent).apply {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    (getItem(bindingAdapterPosition) as BaseAccountItem.ContactItem).run {
                        listener.onContactItemClick(address)
                    }
                }
            }
        }
    }

    private fun createAccountItemViewHolder(parent: ViewGroup): AccountSelectionAccountItemViewHolder {
        return AccountSelectionAccountItemViewHolder.create(parent).apply {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    (getItem(bindingAdapterPosition) as BaseAccountItem.AccountItem).run {
                        listener.onAccountItemClick(address)
                    }
                }
            }
        }
    }

    private fun createAccountErrorItemViewHolder(parent: ViewGroup): AccountSelectionAccountErrorItemViewHolder {
        return AccountSelectionAccountErrorItemViewHolder.create(parent)
    }

    private fun createNftDomainAccountItemViewHolder(
        parent: ViewGroup
    ): AccountSelectionNftDomainAccountItemViewHolder {
        return AccountSelectionNftDomainAccountItemViewHolder.create(parent).apply {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    (getItem(bindingAdapterPosition) as BaseAccountItem.NftDomainAccountItem).run {
                        listener.onNftDomainItemClick(
                            accountAddress = address,
                            nftDomain = displayName,
                            logoUrl = serviceLogoUrl
                        )
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AccountSelectionPasteItemViewHolder -> {
                holder.bind(getItem(position) as BaseAccountSelectionListItem.PasteItem)
            }
            is AccountSelectionHeaderItemViewHolder -> {
                holder.bind(getItem(position) as BaseAccountSelectionListItem.HeaderItem)
            }
            is AccountSelectionContactItemViewHolder -> {
                holder.bind(getItem(position) as BaseAccountItem.ContactItem)
            }
            is AccountSelectionAccountItemViewHolder -> {
                holder.bind(getItem(position) as BaseAccountItem.AccountItem)
            }
            is AccountSelectionAccountErrorItemViewHolder -> {
                holder.bind(getItem(position) as BaseAccountItem.AccountErrorItem)
            }
            is AccountSelectionNftDomainAccountItemViewHolder -> {
                holder.bind(getItem(position) as BaseAccountItem.NftDomainAccountItem)
            }
        }
    }

    interface Listener {
        fun onPasteItemClick(publicKey: String) {}
        fun onAccountItemClick(publicKey: String) {}
        fun onContactItemClick(publicKey: String) {}
        fun onNftDomainItemClick(accountAddress: String, nftDomain: String, logoUrl: String?) {}
    }

    companion object {
        private val logTag = AccountSelectionAdapter::class.java
    }
}
