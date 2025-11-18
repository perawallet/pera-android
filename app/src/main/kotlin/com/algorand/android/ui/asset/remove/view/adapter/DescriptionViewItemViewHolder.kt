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
import com.algorand.android.databinding.ItemRemoveAssetDescriptionBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem

class DescriptionViewItemViewHolder(
    private val binding: ItemRemoveAssetDescriptionBinding
) : BaseViewHolder<RemoveAssetHeaderItem>(binding.root) {

    override fun bind(item: RemoveAssetHeaderItem) {
        if (item !is RemoveAssetHeaderItem.DescriptionViewItem) return
        binding.descriptionTextView.setText(item.descriptionTextRes)
    }

    companion object {
        fun create(parent: ViewGroup): DescriptionViewItemViewHolder {
            val binding = ItemRemoveAssetDescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DescriptionViewItemViewHolder(binding)
        }
    }
}
