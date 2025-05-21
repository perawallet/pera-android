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

package com.algorand.android.ui.asset.collectible.listing.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem.ItemType.INFO_VIEW_ITEM
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem.ItemType.SEARCH_VIEW_ITEM
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem.ItemType.TITLE_TEXT_VIEW_ITEM
import com.algorand.android.utils.hideKeyboard

class CollectibleListHeadersAdapter(
    private val listener: CollectibleListHeadersAdapterListener
) : ListAdapter<BaseCollectibleListHeaderItem, BaseViewHolder<BaseCollectibleListHeaderItem>>(
    BaseDiffUtil<BaseCollectibleListHeaderItem>()
) {

    private val infoItemListener = object : InfoItemViewHolder.InfoItemListener {
        override fun primaryButtonOnClickListener() = listener.onManageCollectiblesClick()
        override fun secondaryButtonClickListener() = listener.onReceiveCollectibleItemClick()
    }

    private val searchViewTextChangedListener = object : SearchViewItemViewHolder.Listener {
        override fun onSearchViewTextChanged(text: String) {
            listener.onSearchQueryUpdated(text)
        }

        override fun onLinearVerticalListingOptionSelected() {
            listener.onLinearVerticalListingOptionSelected()
        }

        override fun onGridListingOptionSelected() {
            listener.onGridListingOptionSelected()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseCollectibleListHeaderItem> {
        return when (viewType) {
            TITLE_TEXT_VIEW_ITEM.value -> createTitleViewHolder(parent)
            INFO_VIEW_ITEM.value -> createInfoViewHolder(parent)
            SEARCH_VIEW_ITEM.value -> createSearchViewHolder(parent)
            else -> throw IllegalArgumentException("$logTag: Unknown Item Type -> $viewType")
        }
    }

    private fun createTitleViewHolder(parent: ViewGroup): BaseViewHolder<BaseCollectibleListHeaderItem> {
        return TitleTextItemViewHolder.create(parent)
    }

    private fun createInfoViewHolder(parent: ViewGroup): BaseViewHolder<BaseCollectibleListHeaderItem> {
        return InfoItemViewHolder.create(parent, infoItemListener = infoItemListener)
    }

    private fun createSearchViewHolder(parent: ViewGroup): BaseViewHolder<BaseCollectibleListHeaderItem> {
        return SearchViewItemViewHolder.create(parent, listener = searchViewTextChangedListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseCollectibleListHeaderItem>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<BaseCollectibleListHeaderItem>) {
        super.onViewDetachedFromWindow(holder)
        if (holder is SearchViewItemViewHolder) {
            holder.itemView.hideKeyboard()
        }
    }

    interface CollectibleListHeadersAdapterListener {
        fun onReceiveCollectibleItemClick()
        fun onSearchQueryUpdated(query: String)
        fun onManageCollectiblesClick()
        fun onLinearVerticalListingOptionSelected()
        fun onGridListingOptionSelected()
    }

    companion object {
        private val logTag = CollectibleListHeadersAdapter::class.java.simpleName
    }
}
