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

package com.algorand.android.banner.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.algorand.android.databinding.ItemSpotBannerBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.ui.spotbanner.view.SpotBannerCarouselListener

class SpotBannerViewHolder(
    private val binding: ItemSpotBannerBinding
) : BaseViewHolder<BaseAccountListItem>(binding.root) {

    override fun bind(item: BaseAccountListItem) {
        if (item !is BaseAccountListItem.SpotBannerItem) return
        binding.root.updateData(item.spotBanners)
    }

    companion object {
        fun create(parent: ViewGroup, listener: SpotBannerCarouselListener): SpotBannerViewHolder {
            val binding = ItemSpotBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.root.setListener(listener)
            return SpotBannerViewHolder(binding)
        }
    }
}
