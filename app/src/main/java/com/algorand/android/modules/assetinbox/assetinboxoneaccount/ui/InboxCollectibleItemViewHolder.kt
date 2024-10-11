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
 *
 */

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.algorand.android.R
import com.algorand.android.databinding.ItemInboxCollectibleBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview

class InboxCollectibleItemViewHolder(
    private val binding: ItemInboxCollectibleBinding,
    private val listener: InboxAsaSelectionAdapter.Listener
) : BaseViewHolder<AsaPreview>(binding.root) {

    override fun bind(item: AsaPreview) {
        if (item !is AsaPreview.CollectiblePreview) return

        with(binding) {
            with(item) {
                root.setOnClickListener { listener.onAsaItemClick(item) }
                assetNameTextView.text = assetName.getName()
                amountTextView.apply {
                    text = resources.getString(R.string.x_number_of, amount)
                }
                firstSenderTextView.text = firstSender
                otherSendersTextView.apply {
                    isVisible = otherSendersCount > 0
                    text = resources.getString(R.string.plus_number_more, otherSendersCount)
                }
                assetDrawableProvider?.provideAssetDrawable(
                    imageView = collectibleIconImageView,
                    onResourceFailed = ::setStartIconDrawable
                )
            }
        }
    }

    fun setStartIconDrawable(drawable: Drawable?) {
        binding.collectibleIconImageView.apply {
            isVisible = drawable != null
            setImageDrawable(drawable)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            listener: InboxAsaSelectionAdapter.Listener
        ): InboxCollectibleItemViewHolder {
            val binding = ItemInboxCollectibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return InboxCollectibleItemViewHolder(binding, listener)
        }
    }
}
