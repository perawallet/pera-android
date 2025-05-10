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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.nft.utils.NFTItemClickListener
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem.ItemType.GRID_SIMPLE_NFT_ITEM
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem.ItemType.GRID_SIMPLE_PENDING_ITEM
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem.ItemType.LINEAR_VERTICAL_SIMPLE_NFT_ITEM
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem.ItemType.LINEAR_VERTICAL_SIMPLE_PENDING_ITEM
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem.ItemType.PLACEHOLDER_ITEM

class CollectibleListAdapter(
    private val listener: CollectibleListAdapterListener
) : PagingDataAdapter<CollectibleListItem, BaseViewHolder<CollectibleListItem>>(BaseDiffUtil<CollectibleListItem>()) {

    private val ownedNFTClickItemListener = NFTItemClickListener { nftId, nftOwnerId ->
        listener.onOwnedNFTItemClick(nftId, nftOwnerId)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return PLACEHOLDER_ITEM.value
        return getItem(position)?.itemType?.value ?: PLACEHOLDER_ITEM.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CollectibleListItem> {
        return when (viewType) {
            LINEAR_VERTICAL_SIMPLE_NFT_ITEM.value -> createLinearVerticalSimpleNFTViewHolder(parent)
            LINEAR_VERTICAL_SIMPLE_PENDING_ITEM.value -> createLinearVerticalSimplePendingNFTViewHolder(parent)
            GRID_SIMPLE_NFT_ITEM.value -> createGridSimpleNFTViewHolder(parent)
            GRID_SIMPLE_PENDING_ITEM.value -> createGridSimplePendingNFTViewHolder(parent)
            PLACEHOLDER_ITEM.value -> PagingDataAdapterPlaceholderViewHolder.create(parent)
            else -> throw IllegalArgumentException("$logTag: Unknown Item Type -> $viewType")
        }
    }

    private fun createLinearVerticalSimpleNFTViewHolder(
        parent: ViewGroup
    ): BaseViewHolder<CollectibleListItem> {
        return OwnedNFTLinearVerticalViewHolder.create(parent, ownedNFTClickItemListener)
    }

    private fun createLinearVerticalSimplePendingNFTViewHolder(
        parent: ViewGroup
    ): BaseViewHolder<CollectibleListItem> {
        return PendingNFTLinearVerticalViewHolder.create(parent)
    }

    private fun createGridSimpleNFTViewHolder(parent: ViewGroup): BaseViewHolder<CollectibleListItem> {
        return OwnedNFTGridViewHolder.create(parent, ownedNFTClickItemListener)
    }

    private fun createGridSimplePendingNFTViewHolder(parent: ViewGroup): BaseViewHolder<CollectibleListItem> {
        return PendingNFTGridViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<CollectibleListItem>, position: Int) {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    interface CollectibleListAdapterListener {
        fun onOwnedNFTItemClick(collectibleAssetId: Long, publicKey: String)
    }

    companion object {
        private val logTag = CollectibleListAdapter::class.java.simpleName
    }
}
