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

package com.algorand.android.ui.rekeyedaccounts.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.algorand.android.databinding.ItemRekeyedAccountSelectionTitleBinding
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem
import com.algorand.android.utils.getXmlStyledPluralString

class RekeyedAccountSelectionTitleViewHolder(
    private val binding: ItemRekeyedAccountSelectionTitleBinding
) : ViewHolder(binding.root) {

    fun bind(item: RekeyedAccountSelectionItem.TitleItem) {
        binding.rekeyedAccountSelectionTitleTextView.apply {
            text = context.getXmlStyledPluralString(item.title)
        }
    }

    companion object {
        fun create(parent: ViewGroup): RekeyedAccountSelectionTitleViewHolder {
            val binding = ItemRekeyedAccountSelectionTitleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return RekeyedAccountSelectionTitleViewHolder(binding)
        }
    }
}
