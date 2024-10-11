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

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accounts.ui.adapter.AccountAdapter
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.ItemType

class InboxAsaSelectionAdapter(
    private val listener: Listener
) : ListAdapter<AsaPreview, BaseViewHolder<AsaPreview>>(BaseDiffUtil()) {

    private val asaClickListener = object : Listener {
        override fun onAsaItemClick(asaPreview: AsaPreview) {
            listener.onAsaItemClick(asaPreview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<AsaPreview> {
        return when (viewType) {
            ItemType.ASSET.ordinal -> InboxAssetItemViewHolder.create(parent, asaClickListener)
            ItemType.COLLECTIBLE.ordinal -> InboxCollectibleItemViewHolder.create(parent, asaClickListener)
            else -> throw Exception("$logTag: Item View Type is Unknown.")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.ordinal
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AsaPreview>, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onAsaItemClick(asaPreview: AsaPreview) {}
    }

    companion object {
        private val logTag = AccountAdapter::class.java.simpleName
    }
}
