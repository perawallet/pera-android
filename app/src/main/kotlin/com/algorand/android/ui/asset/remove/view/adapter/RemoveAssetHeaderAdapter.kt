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

package com.algorand.android.ui.asset.remove.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem.ItemType.DESCRIPTION_VIEW_ITEM
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem.ItemType.SEARCH_VIEW_ITEM
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem.ItemType.TITLE_VIEW_ITEM
import com.algorand.android.utils.hideKeyboard

class RemoveAssetHeaderAdapter(
    private val listener: RemoveAssetHeaderAdapterListener
) : ListAdapter<RemoveAssetHeaderItem, BaseViewHolder<RemoveAssetHeaderItem>>(BaseDiffUtil()) {

    private val searchViewItemListener = SearchViewItemViewHolder.SearchViewItemListener {
        listener.onSearchQueryUpdate(it)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.itemType?.value ?: RecyclerView.NO_POSITION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RemoveAssetHeaderItem> {
        return when (viewType) {
            SEARCH_VIEW_ITEM.value -> SearchViewItemViewHolder.create(parent, searchViewItemListener)
            TITLE_VIEW_ITEM.value -> TitleViewItemViewHolder.create(parent)
            DESCRIPTION_VIEW_ITEM.value -> DescriptionViewItemViewHolder.create(parent)
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RemoveAssetHeaderItem>, position: Int) {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<RemoveAssetHeaderItem>) {
        super.onViewDetachedFromWindow(holder)
        if (holder is SearchViewItemViewHolder) {
            holder.itemView.hideKeyboard()
        }
    }

    interface RemoveAssetHeaderAdapterListener {
        fun onSearchQueryUpdate(query: String)
    }
}
