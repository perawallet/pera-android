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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accountdetail.assets.ui.adapter.AccountAssetsAdapter
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.ItemType.PLACEHOLDER_ITEM
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.ItemType.REMOVE_ASSET_ITEM
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.ItemType.REMOVE_COLLECTIBLE_ITEM
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.ItemType.SCREEN_STATE_ITEM
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.RemoveAssetItem
import com.algorand.android.ui.asset.remove.view.adapter.RemoveAssetItemViewHolder.AssetRemovalItemListener
import com.algorand.android.ui.asset.remove.view.adapter.RemoveCollectibleItemViewHolder.CollectibleRemovalItemListener
import com.algorand.android.ui.common.listhelper.viewholders.PagingPlaceholderViewHolder

class RemoveAssetAdapter(
    private val listener: RemoveAssetAdapterListener
) : PagingDataAdapter<BaseRemoveAssetItem, BaseViewHolder<BaseRemoveAssetItem>>(BaseDiffUtil()) {

    private val assetRemovalItemListener = object : AssetRemovalItemListener {
        override fun onActionButtonClick(removeAssetListItem: RemoveAssetItem) {
            listener.onAssetRemoveClick(removeAssetListItem)
        }

        override fun onItemClick(assetId: Long) {
            listener.onAssetItemClick(assetId)
        }
    }

    private val collectibleItemListener = object : CollectibleRemovalItemListener {
        override fun onActionButtonClick(removeAssetListItem: RemoveAssetItem) {
            listener.onCollectibleRemoveClick(removeAssetListItem)
        }

        override fun onItemClick(collectibleId: Long) {
            listener.onCollectibleItemClick(collectibleId)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return PLACEHOLDER_ITEM.value
        return getItem(position)?.itemType?.value ?: PLACEHOLDER_ITEM.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseRemoveAssetItem> {
        return when (viewType) {
            REMOVE_ASSET_ITEM.value -> RemoveAssetItemViewHolder.create(parent, assetRemovalItemListener)
            REMOVE_COLLECTIBLE_ITEM.value -> RemoveCollectibleItemViewHolder.create(parent, collectibleItemListener)
            SCREEN_STATE_ITEM.value -> ScreenStateViewHolder.create(parent)
            PLACEHOLDER_ITEM.value -> PagingPlaceholderViewHolder.create(parent)
            else -> throw IllegalArgumentException("$logTag: Unknown viewType = $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseRemoveAssetItem>, position: Int) {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    interface RemoveAssetAdapterListener {
        fun onAssetItemClick(assetId: Long)
        fun onCollectibleItemClick(collectibleId: Long)
        fun onCollectibleRemoveClick(removeAssetItem: RemoveAssetItem)
        fun onAssetRemoveClick(removeAssetItem: RemoveAssetItem)
    }

    companion object {
        private val logTag = AccountAssetsAdapter::class.java.simpleName
    }
}
