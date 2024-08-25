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

package com.algorand.android.modules.swap.assetselection.base

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.algorand.android.designsystem.BaseDiffUtil
import com.algorand.android.modules.swap.assetselection.base.SwapAssetSelectionViewHolder.SwapAssetSelectionViewHolderListener
import com.algorand.android.swapui.assetselection.model.SwapAssetSelectionItem

class SwapAssetSelectionAdapter(
    private val listener: SwapAssetSelectionAdapterListener
) : ListAdapter<SwapAssetSelectionItem, SwapAssetSelectionViewHolder>(BaseDiffUtil()) {

    private val swapAssetSelectionViewHolderListener = object : SwapAssetSelectionViewHolderListener {
        override fun onAssetItemSelected(item: SwapAssetSelectionItem) {
            listener.onAssetSelected(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwapAssetSelectionViewHolder {
        return SwapAssetSelectionViewHolder.create(parent, swapAssetSelectionViewHolderListener)
    }

    override fun onBindViewHolder(holder: SwapAssetSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface SwapAssetSelectionAdapterListener {
        fun onAssetSelected(item: SwapAssetSelectionItem)
    }
}
