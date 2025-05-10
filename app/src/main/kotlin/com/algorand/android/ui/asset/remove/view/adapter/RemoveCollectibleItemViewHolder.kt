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

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.databinding.ItemRemoveCollectibleBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.RemoveAssetItem
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider

class RemoveCollectibleItemViewHolder(
    private val binding: ItemRemoveCollectibleBinding,
    private val listener: CollectibleRemovalItemListener
) : BaseViewHolder<BaseRemoveAssetItem>(binding.root) {

    override fun bind(item: BaseRemoveAssetItem) {
        if (item !is RemoveAssetItem) return
        with(binding.collectibleStatefulItemView) {
            with(item) {
                bindImage(baseAssetDrawableProvider = baseAssetDrawableProvider)
                setTitleText(title = name.assetName)
                setDescriptionText(description = shortName.assetName)
                setPrimaryValueText(primaryValue = formattedCompactAmount)
                setSecondaryValueText(secondaryValue = formattedSelectedCurrencyCompactValue)
                setButtonState(state = actionItemButtonState)
                setClickListeners(removeCollectibleListItem = this)
            }
        }
    }

    private fun bindImage(baseAssetDrawableProvider: BaseAssetDrawableProvider) {
        binding.collectibleStatefulItemView.apply {
            getStartIconImageView().apply {
                baseAssetDrawableProvider.provideAssetDrawable(
                    imageView = this,
                    onResourceFailed = ::setStartIconDrawable
                )
            }
        }
    }

    private fun setClickListeners(removeCollectibleListItem: RemoveAssetItem) {
        with(binding.collectibleStatefulItemView) {
            setOnClickListener { listener.onItemClick(removeCollectibleListItem.id) }
            setActionButtonClickListener { listener.onActionButtonClick(removeCollectibleListItem) }
        }
    }

    interface CollectibleRemovalItemListener {
        fun onActionButtonClick(removeAssetListItem: RemoveAssetItem)
        fun onItemClick(collectibleId: Long)
    }

    companion object {
        fun create(parent: ViewGroup, listener: CollectibleRemovalItemListener): RemoveCollectibleItemViewHolder {
            return RemoveCollectibleItemViewHolder(
                ItemRemoveCollectibleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                listener
            )
        }
    }
}
