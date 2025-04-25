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

package com.algorand.android.modules.assets.action.base

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.buildSpannedString
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.customviews.toolbar.CustomToolbar
import com.algorand.android.databinding.BottomSheetAssetActionBinding
import com.algorand.android.models.BaseAccountAddress
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.Resource
import com.algorand.android.utils.addUnnamedAssetName
import com.algorand.android.utils.copyToClipboard
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.setAssetNameTextColorByVerificationTier
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.google.android.material.button.MaterialButton

// TODO Refactor this class whenever have a time
abstract class BaseAssetActionBottomSheet : BaseBottomSheet(R.layout.bottom_sheet_asset_action) {

    private val viewStateCollector: suspend (BaseAssetActionViewModel.ViewState) -> Unit = { state ->
        when (state) {
            is BaseAssetActionViewModel.ViewState.Idle -> Unit
            is BaseAssetActionViewModel.ViewState.DefaultState -> setAccountName(state.accountAddress)
        }
    }

    protected val binding by viewBinding(BottomSheetAssetActionBinding::bind)

    abstract val assetActionViewModel: BaseAssetActionViewModel

    protected var asset: Asset? = null

    private val assetCollector: suspend (Resource<Asset>?) -> Unit = { resource ->
        resource?.use(
            onSuccess = { assetDescription ->
                asset = assetDescription
                setAssetDetails(assetDescription)
            },
            onLoading = { binding.loadingProgressBar.visibility = View.VISIBLE },
            onLoadingFinished = { binding.loadingProgressBar.visibility = View.GONE },
            onFailed = { showErrorAndNavBack(it) }
        )
    }

    abstract fun setDescriptionTextView(textView: TextView)
    abstract fun setToolbar(customToolbar: CustomToolbar)
    abstract fun setPositiveButton(materialButton: MaterialButton)
    abstract fun setNegativeButton(materialButton: MaterialButton)

    open fun setTransactionFeeTextView(textView: TextView) {}
    open fun setWarningIconImageView(imageView: ImageView) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initObservers()
        initUi()
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
        collectLatestOnLifecycle(
            flow = assetActionViewModel.assetFlow,
            collection = assetCollector
        )

        collectLatestOnLifecycle(
            assetActionViewModel.state,
            viewStateCollector
        )
    }

    private fun setAssetDetails(asset: Asset) {
        with(binding) {
            with(asset) {
                assetFullNameTextView.text = fullName
                updateAssetShortNameTextView(shortName, verificationTier)
                assetIdTextView.text = id.toString()
                copyIDButton.setOnClickListener { onCopyClick() }
            }
        }
    }

    private fun updateAssetShortNameTextView(shortName: String?, verificationTier: VerificationTier?) {
        binding.assetShortNameTextView.apply {
            text = if (shortName.isNullOrBlank()) {
                buildSpannedString { context?.let { addUnnamedAssetName(it) } }
            } else {
                shortName
            }
            assetActionViewModel.getVerificationTierConfiguration(verificationTier).run {
                setAssetNameTextColorByVerificationTier(this@run)
                if (drawableResId != null) {
                    setDrawable(end = AppCompatResources.getDrawable(context, drawableResId))
                }
            }
        }
    }

    private fun onCopyClick() {
        context?.copyToClipboard(assetActionViewModel.assetId.toString(), ASSET_ID_COPY_LABEL)
    }

    private fun showErrorAndNavBack(error: Resource.Error) {
        context?.run {
            val errorMessage = error.parse(this).toString()
            showGlobalError(errorMessage = errorMessage, tag = baseActivityTag)
            navBack()
        }
    }

    private fun setAccountName(accountAddress: BaseAccountAddress.AccountAddress) {
        binding.accountTextView.apply {
            with(accountAddress) {
                text = getDisplayAddress()
                setDrawable(
                    start = AccountIconDrawable.create(
                        context = context,
                        accountIconDrawablePreview = accountIconDrawablePreview,
                        sizeResId = R.dimen.spacing_xlarge
                    )
                )
                setOnLongClickListener { onAccountAddressCopied(publicKey); true }
            }
        }
    }

    companion object {
        private const val ASSET_ID_COPY_LABEL = "asset_id_label"
    }
}
