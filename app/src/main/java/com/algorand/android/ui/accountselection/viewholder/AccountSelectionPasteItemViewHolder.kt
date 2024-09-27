/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.accountselection.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.databinding.ItemPasteAddressBinding
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem

class AccountSelectionPasteItemViewHolder(
    private val binding: ItemPasteAddressBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BaseAccountSelectionListItem.PasteItem) {
        binding.copiedAddressTextView.text = item.address
    }

    companion object {
        fun create(parent: ViewGroup): AccountSelectionPasteItemViewHolder {
            val binding = ItemPasteAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AccountSelectionPasteItemViewHolder(binding)
        }
    }
}
