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

package com.algorand.android.modules.transactionhistory.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem.ItemType.APPLICATION_CALL_ITEM
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem.ItemType.PLACEHOLDER_ITEM
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem.ItemType.RESOURCE_TITLE_ITEM
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem.ItemType.STRING_TITLE_ITEM
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem.ItemType.TRANSACTION_ITEM
import com.algorand.android.modules.transactionhistory.ui.viewholder.AccountHistoryPagingPlaceholderViewHolder
import com.algorand.android.modules.transactionhistory.ui.viewholder.AccountHistoryResourceTitleViewHolder
import com.algorand.android.modules.transactionhistory.ui.viewholder.AccountHistoryStringTitleViewHolder
import com.algorand.android.modules.transactionhistory.ui.viewholder.AccountHistoryTransactionItemViewHolder
import com.algorand.android.modules.transactionhistory.ui.viewholder.ApplicationCallItemViewHolder

class AccountHistoryAdapter(
    private val listener: Listener
) : PagingDataAdapter<BaseTransactionItem, BaseViewHolder<BaseTransactionItem>>(BaseDiffUtil()) {

    private val applicationCallItemListener = ApplicationCallItemViewHolder.ApplicationCallItemListener {
        listener.onApplicationCallTransactionClick(it)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return PLACEHOLDER_ITEM.value
        return getItem(position)?.itemType?.value ?: PLACEHOLDER_ITEM.value
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseTransactionItem>, position: Int) {
        if (position != -1 && position < itemCount) {
            getItem(position)?.let { holder.bind(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseTransactionItem> {
        return when (viewType) {
            STRING_TITLE_ITEM.value -> createHistoryStringTitleViewHolder(parent)
            RESOURCE_TITLE_ITEM.value -> createHistoryResourceTitleViewHolder(parent)
            APPLICATION_CALL_ITEM.value -> createApplicationCallItemViewHolder(parent)
            TRANSACTION_ITEM.value -> createHistoryItemViewHolder(parent)
            PLACEHOLDER_ITEM.value -> createPlaceholderViewHolder(parent)
            else -> throw IllegalArgumentException("$logTag : Item View Type is Unknown.")
        }
    }

    fun getTitleForPosition(position: Int): String? {
        if (position in 0 until itemCount) {
            var currentPosition = position
            while (currentPosition >= 0) {
                val currentItem = getItem(currentPosition)
                if (currentItem is BaseTransactionItem.StringTitleItem) return currentItem.title
                currentPosition--
            }
        }
        return null
    }

    private fun createApplicationCallItemViewHolder(parent: ViewGroup): ApplicationCallItemViewHolder {
        return ApplicationCallItemViewHolder.create(parent, applicationCallItemListener)
    }

    private fun createHistoryStringTitleViewHolder(parent: ViewGroup): AccountHistoryStringTitleViewHolder {
        return AccountHistoryStringTitleViewHolder.create(parent)
    }

    private fun createHistoryResourceTitleViewHolder(parent: ViewGroup): AccountHistoryResourceTitleViewHolder {
        return AccountHistoryResourceTitleViewHolder.create(parent)
    }

    private fun createPlaceholderViewHolder(parent: ViewGroup): AccountHistoryPagingPlaceholderViewHolder {
        return AccountHistoryPagingPlaceholderViewHolder.create(parent)
    }

    private fun createHistoryItemViewHolder(parent: ViewGroup): AccountHistoryTransactionItemViewHolder {
        return AccountHistoryTransactionItemViewHolder.create(parent).apply {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener.onStandardTransactionClick(
                        getItem(bindingAdapterPosition) as BaseTransactionItem.TransactionItem
                    )
                }
            }
        }
    }

    interface Listener {
        fun onStandardTransactionClick(transaction: BaseTransactionItem.TransactionItem)
        fun onApplicationCallTransactionClick(transaction: BaseTransactionItem.TransactionItem.ApplicationCallItem)
    }

    companion object {
        private val logTag = AccountHistoryAdapter::class.java.simpleName
    }
}
