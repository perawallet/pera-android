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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.algorand.android.R
import com.algorand.android.databinding.ItemInboxAssetBinding
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview
import com.algorand.android.utils.setAssetNameTextColorByVerificationTier
import com.algorand.android.utils.setDrawable

class InboxAssetItemViewHolder(
    private val binding: ItemInboxAssetBinding,
    private val listener: InboxAsaSelectionAdapter.Listener
) : BaseViewHolder<AsaPreview>(binding.root) {

    override fun bind(item: AsaPreview) {
        if (item !is AsaPreview.AssetPreview) return
        with(binding) {
            with(item) {
                root.setOnClickListener { listener.onAsaItemClick(item) }

                assetNameTextView.apply {
                    text = assetName.getName()
                    setAssetNameTextColorByVerificationTier(verificationTierConfiguration)
                    val verificationTierDrawable = verificationTierConfiguration.drawableResId?.let { drawableResId ->
                        AppCompatResources.getDrawable(context, drawableResId)
                    }
                    setDrawable(end = verificationTierDrawable)
                }

                amountTextView.text = formattedAssetAmount

                firstSenderTextView.apply {
                    isVisible = firstSender.isNotBlank()
                    text = firstSender
                }

                otherSendersTextView.apply {
                    isVisible = otherSendersCount > 0
                    text = resources.getString(R.string.plus_number_more, otherSendersCount)
                }

                usdValueTextView.text = usdValue

                assetDrawableProvider?.provideAssetDrawable(
                    imageView = assetIconImageView,
                    onResourceFailed = ::setStartIconDrawable
                )
            }
        }
    }

    fun setStartIconDrawable(drawable: Drawable?) {
        binding.assetIconImageView.apply {
            isVisible = drawable != null
            setImageDrawable(drawable)
        }
    }

    companion object {
        fun create(parent: ViewGroup, listener: InboxAsaSelectionAdapter.Listener): InboxAssetItemViewHolder {
            val binding = ItemInboxAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return InboxAssetItemViewHolder(binding, listener)
        }
    }
}
