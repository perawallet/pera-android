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

package com.algorand.android.modules.collectibles.action.optin

import android.widget.TextView
import androidx.fragment.app.viewModels
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.customviews.toolbar.CustomToolbar
import com.algorand.android.models.AssetActionResult
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assets.action.base.BaseAssetActionBottomSheet
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.setFragmentNavigationResult
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectibleOptInActionBottomSheet : BaseAssetActionBottomSheet() {

    private val toolbarConfiguration = ToolbarConfiguration(titleResId = R.string.opt_in_to_nft)

    override val assetActionViewModel: CollectibleOptInActionViewModel by viewModels()

    override fun initUi() {
        with(binding) {
            transactionFeeGroup.show()
            accountGroup.show()
        }
        assetActionViewModel.getAccountName(assetActionViewModel.accountAddress)
    }

    override fun setDescriptionTextView(textView: TextView) {
        textView.setText(R.string.opting_in_to_an_nft)
    }

    override fun setToolbar(customToolbar: CustomToolbar) {
        customToolbar.configure(toolbarConfiguration)
    }

    override fun setPositiveButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.approve)
            setOnClickListener {
                asset?.let { assetDescription ->
                    val assetActionResult = AssetActionResult(
                        assetId = assetDescription.id,
                        publicKey = assetActionViewModel.accountAddress
                    )
                    (activity as? MainActivity)?.signAddAssetTransaction(assetActionResult)
                    setFragmentNavigationResult(OPT_IN_COLLECTIBLE_ACTION_RESULT_KEY, true)
                }
                navBack()
            }
        }
    }

    override fun setNegativeButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.close)
            setOnClickListener { navBack() }
        }
    }

    override fun setTransactionFeeTextView(textView: TextView) {
        textView.text = assetActionViewModel.getTransactionFee()
    }

    companion object {
        const val OPT_IN_COLLECTIBLE_ACTION_RESULT_KEY: String = "opt_in_collectible_action_result_key"
    }
}
