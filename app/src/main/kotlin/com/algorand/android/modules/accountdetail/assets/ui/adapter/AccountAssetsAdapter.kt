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

package com.algorand.android.modules.accountdetail.assets.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.ASSET
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.NFT
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.NO_ASSET_FOUND
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.PENDING_ASSET
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.PENDING_NFT
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.ItemType.PLACEHOLDER_ITEM
import com.algorand.android.ui.common.listhelper.viewholders.PagingPlaceholderViewHolder

class AccountAssetsAdapter(
    private val listener: Listener
) : PagingDataAdapter<AccountDetailAssetsItem, BaseViewHolder<AccountDetailAssetsItem>>(BaseDiffUtil()) {

    private val ownedAssetViewHolderListener = object : OwnedAssetViewHolder.Listener {
        override fun onOwnedAssetItemClick(assetId: Long) {
            listener.onAssetClick(assetId)
        }

        override fun onOwnedAssetLongPressed(assetId: Long) {
            listener.onAssetLongClick(assetId)
        }
    }

    private val ownedNFTViewHolderListener = object : OwnedNFTViewHolder.Listener {
        override fun onOwnedNFTItemClick(nftId: Long) {
            listener.onNFTClick(nftId)
        }

        override fun onOwnedNFTItemLongPressed(nftId: Long) {
            listener.onNFTLongClick(nftId)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION || position >= itemCount) return PLACEHOLDER_ITEM.viewType
        return getItem(position)?.itemType?.viewType ?: PLACEHOLDER_ITEM.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<AccountDetailAssetsItem> {
        return when (viewType) {
            ASSET.viewType -> createOwnedAssetViewHolder(parent)
            PENDING_ASSET.viewType -> createPendingAssetViewHolder(parent)
            NO_ASSET_FOUND.viewType -> createNoAssetFoundScreenStateViewHolder(parent)
            NFT.viewType -> createOwnedNFTViewHolder(parent)
            PENDING_NFT.viewType -> createPendingNFTViewHolder(parent)
            PLACEHOLDER_ITEM.viewType -> createPlaceholderViewHolder(parent)
            else -> throw IllegalArgumentException("$logTag : Item View Type is Unknown.")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AccountDetailAssetsItem>, position: Int) {
        if (position != -1 && position < itemCount) {
            getItem(position)?.let { holder.bind(it) }
        }
    }

    private fun createOwnedAssetViewHolder(parent: ViewGroup): OwnedAssetViewHolder {
        return OwnedAssetViewHolder.create(parent, ownedAssetViewHolderListener)
    }

    private fun createPendingAssetViewHolder(parent: ViewGroup): PendingAssetViewHolder {
        return PendingAssetViewHolder.create(parent)
    }

    private fun createNoAssetFoundScreenStateViewHolder(parent: ViewGroup): NoAssetFoundScreenStateViewHolder {
        return NoAssetFoundScreenStateViewHolder.create(parent)
    }

    private fun createOwnedNFTViewHolder(parent: ViewGroup): OwnedNFTViewHolder {
        return OwnedNFTViewHolder.create(parent, ownedNFTViewHolderListener)
    }

    private fun createPendingNFTViewHolder(parent: ViewGroup): PendingNFTViewHolder {
        return PendingNFTViewHolder.create(parent)
    }

    private fun createPlaceholderViewHolder(parent: ViewGroup): PagingPlaceholderViewHolder<AccountDetailAssetsItem> {
        return PagingPlaceholderViewHolder.create(parent)
    }

    interface Listener {
        fun onAssetClick(assetId: Long)
        fun onAssetLongClick(assetId: Long)
        fun onNFTClick(nftId: Long)
        fun onNFTLongClick(nftId: Long)
    }

    companion object {
        private val logTag = AccountAssetsAdapter::class.simpleName
    }
}
