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

package com.algorand.android.modules.sorting.accountsorting.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.sorting.accountsorting.domain.model.BaseAccountSortingListItem
import com.algorand.android.modules.sorting.accountsorting.ui.viewholder.SortTypeViewHolder

class SortTypeAdapter(
    private val sortTypeAdapterListener: SortTypeAdapterListener
) : ListAdapter<BaseAccountSortingListItem, BaseViewHolder<BaseAccountSortingListItem>>(BaseDiffUtil()) {

    private val sortingTypeItemListener = SortTypeViewHolder.SortingTypeListener {
        sortTypeAdapterListener.onSortingTypeItemClicked(it)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.itemType?.ordinal ?: RecyclerView.NO_POSITION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseAccountSortingListItem> {
        return when (viewType) {
            BaseAccountSortingListItem.ItemType.SORTING_TYPE.ordinal -> createSortingTypeViewHolder(parent)
            else -> throw Exception("$logTag list item is unknown")
        }
    }

    private fun createSortingTypeViewHolder(parent: ViewGroup): SortTypeViewHolder {
        return SortTypeViewHolder.create(parent, sortingTypeItemListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseAccountSortingListItem>, position: Int) {
        holder.bind(getItem(position))
    }

    fun interface SortTypeAdapterListener {
        fun onSortingTypeItemClicked(accountSortingType: AccountSortingTypeIdentifier)
    }

    companion object {
        private val logTag = SortTypeAdapterListener::class.java
    }
}
