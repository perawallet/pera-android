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

package com.algorand.android.modules.assets.action.base

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.algorand.android.R
import com.algorand.android.module.asset.action.ui.AssetActionViewModel
import com.algorand.android.module.asset.action.ui.model.AssetActionAccountDetail
import com.algorand.android.module.asset.action.ui.model.AssetActionInformation
import com.algorand.android.module.asset.action.ui.model.AssetActionPreview
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.customviews.toolbar.CustomToolbar
import com.algorand.android.databinding.BottomSheetAssetActionBinding
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.lifecycle.collectLatestOnLifecycle
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.copyToClipboard
import com.algorand.android.utils.setAssetNameTextColorByVerificationTier
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding
import com.google.android.material.button.MaterialButton

abstract class BaseAssetActionBottomSheet : BaseBottomSheet(R.layout.bottom_sheet_asset_action) {

    protected val binding by viewBinding(BottomSheetAssetActionBinding::bind)

    abstract val assetActionViewModel: AssetActionViewModel

    private val assetActionPreviewCollector: suspend (AssetActionPreview?) -> Unit = {
        if (it != null) initPreview(it)
    }

    private fun initPreview(preview: AssetActionPreview) {
        setAssetDetails(preview.assetActionInformation)
        initAccountDetailTextView(preview.accountDetail)
        preview.showError?.let { showErrorAndNavBack(it) }
    }

    abstract fun setDescriptionTextView(textView: TextView)
    abstract fun setToolbar(customToolbar: CustomToolbar)
    abstract fun setPositiveButton(materialButton: MaterialButton)
    abstract fun setNegativeButton(materialButton: MaterialButton)

    open fun setTransactionFeeTextView(textView: TextView) {}
    open fun setWarningIconImageView(imageView: ImageView) {}

    protected fun getAssetId(): Long = assetActionViewModel.assetId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initUi()
        initObservers()
        with(binding) {
            setDescriptionTextView(descriptionTextView)
            setToolbar(customToolbar)
            setPositiveButton(positiveButton)
            setNegativeButton(negativeButton)
            setTransactionFeeTextView(transactionFeeTextView)
            setWarningIconImageView(warningIconImageView)
        }
    }

    open fun initUi() {}

    open fun initArgs() {}

    open fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            assetActionViewModel.assetActionPreviewFlow,
            assetActionPreviewCollector
        )
    }

    private fun setAssetDetails(asset: AssetActionInformation?) {
        if (asset == null) return
        with(binding) {
            assetIdTextView.text = asset.assetId.toString()
            assetFullNameTextView.text = asset.fullName
            binding.assetShortNameTextView.apply {
                text = asset.shortName
                asset.verificationTierConfiguration.run {
                    setAssetNameTextColorByVerificationTier(this)
                    if (drawableResId != null) {
                        setDrawable(end = AppCompatResources.getDrawable(context, drawableResId!!))
                    }
                }
            }
            copyIDButton.setOnClickListener { onCopyClick(asset.assetId) }
        }
    }

    private fun initAccountDetailTextView(assetActionAccountDetail: AssetActionAccountDetail?) {
        if (assetActionAccountDetail == null) return
        binding.accountTextView.apply {
            text = assetActionAccountDetail.displayName
            setDrawable(
                start = AccountIconDrawable.create(
                    context = context,
                    accountIconDrawablePreview = assetActionAccountDetail.iconDrawablePreview,
                    sizeResId = R.dimen.spacing_xlarge
                )
            )
            setOnLongClickListener { onAccountAddressCopied(assetActionAccountDetail.address); true }
        }
    }

    private fun onCopyClick(assetId: Long) {
        context?.copyToClipboard(assetId.toString(), ASSET_ID_COPY_LABEL)
    }

    private fun showErrorAndNavBack(error: Event<String?>) {
        context?.run {
            showGlobalError(errorMessage = error.consume(), tag = baseActivityTag)
            navBack()
        }
    }

    companion object {
        private const val ASSET_ID_COPY_LABEL = "asset_id_label"
    }
}
