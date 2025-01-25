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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccountsWithAccount

class InboxAccountSelectionAdapter(
    private val listener: Listener
) : ListAdapter<AssetInboxAllAccountsWithAccount, InboxAccountSelectionViewHolder>(BaseDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxAccountSelectionViewHolder {
        return InboxAccountSelectionViewHolder.create(parent).apply {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener.onAccountItemClick(getItem(bindingAdapterPosition).accountAddress)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: InboxAccountSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onAccountItemClick(publicKey: String) {}
    }
}
