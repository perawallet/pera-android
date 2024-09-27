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

package com.algorand.android.banner.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.databinding.ItemBackupBannerBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accounts.domain.model.BaseAccountListItem
import com.google.android.material.button.MaterialButton

class BackupBannerViewHolder(
    private val binding: ItemBackupBannerBinding,
    private val listener: Listener,
) : BaseViewHolder<BaseAccountListItem>(binding.root) {

    protected open val actionButton: MaterialButton
        get() = binding.bannerActionButton

    override fun bind(item: BaseAccountListItem) {
        if (item !is BaseAccountListItem.BackupBannerItem) return
        initActionButton()
    }

    private fun initActionButton() {
        actionButton?.apply {
            setOnClickListener { listener.onActionButtonClick() }
        }
    }

    interface Listener {
        fun onActionButtonClick() {}
    }

    companion object {
        fun create(parent: ViewGroup, listener: Listener): BackupBannerViewHolder {
            val binding = ItemBackupBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BackupBannerViewHolder(binding, listener)
        }
    }
}
