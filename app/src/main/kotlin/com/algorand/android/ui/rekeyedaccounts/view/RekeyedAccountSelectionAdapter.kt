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

package com.algorand.android.ui.rekeyedaccounts.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AccountItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AuthAddressHeaderItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.DescriptionItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.IconItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.ItemType.ACCOUNT_ITEM
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.ItemType.AUTH_ACCOUNT_HEADER_ITEM
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.ItemType.DESCRIPTION_ITEM
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.ItemType.ICON_ITEM
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.ItemType.TITLE_ITEM
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.TitleItem

class RekeyedAccountSelectionAdapter(
    private val listener: Listener
) : ListAdapter<RekeyedAccountSelectionItem, ViewHolder>(BaseDiffUtil()) {

    private val accountViewHolderListener = object : RekeyedAccountSelectionAccountViewHolder.Listener {
        override fun onAccountItemClick(accountItem: AccountItem) {
            listener.onAccountItemClick(accountItem)
        }

        override fun onAccountItemInformationClick(accountAddress: String) {
            listener.onAccountItemInformationClick(accountAddress)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ICON_ITEM.ordinal -> createIconViewHolder(parent)
            TITLE_ITEM.ordinal -> createTitleViewHolder(parent)
            DESCRIPTION_ITEM.ordinal -> createDescriptionViewHolder(parent)
            ACCOUNT_ITEM.ordinal -> createAccountViewHolder(parent)
            AUTH_ACCOUNT_HEADER_ITEM.ordinal -> createAuthAccountHeaderViewHolder(parent)
            else -> throw IllegalArgumentException("$logTag: Item View Type is Unknown.")
        }
    }

    private fun createIconViewHolder(parent: ViewGroup): RekeyedAccountSelectionIconViewHolder {
        return RekeyedAccountSelectionIconViewHolder.create(parent)
    }

    private fun createTitleViewHolder(parent: ViewGroup): RekeyedAccountSelectionTitleViewHolder {
        return RekeyedAccountSelectionTitleViewHolder.create(parent)
    }

    private fun createDescriptionViewHolder(parent: ViewGroup): RekeyedAccountSelectionDescriptionViewHolder {
        return RekeyedAccountSelectionDescriptionViewHolder.create(parent)
    }

    private fun createAccountViewHolder(parent: ViewGroup): RekeyedAccountSelectionAccountViewHolder {
        return RekeyedAccountSelectionAccountViewHolder.create(parent, accountViewHolderListener)
    }

    private fun createAuthAccountHeaderViewHolder(parent: ViewGroup): RekeyedAccountSelectionAuthAddressViewHolder {
        return RekeyedAccountSelectionAuthAddressViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is RekeyedAccountSelectionIconViewHolder -> holder.bind(getItem(position) as IconItem)
            is RekeyedAccountSelectionTitleViewHolder -> holder.bind(getItem(position) as TitleItem)
            is RekeyedAccountSelectionDescriptionViewHolder -> holder.bind(getItem(position) as DescriptionItem)
            is RekeyedAccountSelectionAccountViewHolder -> holder.bind(getItem(position) as AccountItem)
            is RekeyedAccountSelectionAuthAddressViewHolder -> holder.bind(getItem(position) as AuthAddressHeaderItem)
        }
    }

    interface Listener {
        fun onAccountItemClick(accountItem: AccountItem)
        fun onAccountItemInformationClick(accountAddress: String)
    }

    private companion object {
        val logTag = RekeyedAccountSelectionAdapter::class.simpleName
    }
}
