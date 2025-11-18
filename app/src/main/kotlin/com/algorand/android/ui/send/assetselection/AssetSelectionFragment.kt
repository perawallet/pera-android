/*
 * Copyright 2022-2025 Pera Wallet, LDA
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

package com.algorand.android.ui.send.assetselection

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.transaction.TransactionSignBaseFragment
import com.algorand.android.databinding.FragmentAssetSelectionBinding
import com.algorand.android.models.AssetSelectionOptInPayload
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assetinbox.send.summary.ui.model.Arc59SendSummaryNavArgs
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewEvent.NavToAssetTransferAmountFragment
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewEvent.NavToOptIn
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewState.Content
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewState.Content.ContentStateType
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewState.Loading
import com.algorand.android.ui.send.assetselection.adapter.SelectSendingAssetAdapter
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetSelectionFragment : TransactionSignBaseFragment(R.layout.fragment_asset_selection) {

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.select_the_asset_to_send,
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow
    )

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val binding by viewBinding(FragmentAssetSelectionBinding::bind)

    private val assetSelectionViewModel: AssetSelectionViewModel by viewModels()

    private val assetSelectionAdapter = SelectSendingAssetAdapter(::onAssetClick)

    private val assetSelectionPreviewCollector: suspend (AssetSelectionViewModel.ViewState) -> Unit = {
        updateUiWithPreview(it)
    }

    private val viewEventCollector: suspend (AssetSelectionViewModel.ViewEvent) -> Unit = {
        when (it) {
            is NavToAssetTransferAmountFragment -> navToAssetTransferAmountFragment(it.assetId)
            is NavToOptIn -> navToArc59SendSummaryFragment(it.payload)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showTransactionTipsIfNeed()
        initObservers()
        binding.assetsToSendRecyclerView.adapter = assetSelectionAdapter
        assetSelectionViewModel.initViewState()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            assetSelectionViewModel.state,
            assetSelectionPreviewCollector
        )
        viewLifecycleOwner.collectLatestOnLifecycle(
            assetSelectionViewModel.viewEvent,
            viewEventCollector
        )
    }

    private suspend fun updateUiWithPreview(state: AssetSelectionViewModel.ViewState) {
        val isContentStateTypeLoading = (state is Content && state.type == ContentStateType.Loading)
        binding.progressBar.loadingProgressBar.isVisible = state is Loading || isContentStateTypeLoading
        if (state is Content) {
            assetSelectionAdapter.submitData(state.assetListItems)
        }
    }

    private fun onAssetClick(assetId: Long) {
        assetSelectionViewModel.updatePreviewWithSelectedAsset(assetId)
    }

    private fun navToAssetTransferAmountFragment(assetId: Long) {
        val assetTransaction = assetSelectionViewModel.assetTransaction.copy(assetId = assetId)
        nav(
            AssetSelectionFragmentDirections.actionAssetSelectionFragmentToAssetTransferAmountFragment(
                assetTransaction
            )
        )
    }

    private fun showTransactionTipsIfNeed() {
        if (assetSelectionViewModel.shouldShowTransactionTips()) {
            nav(AssetSelectionFragmentDirections.actionAssetSelectionFragmentToTransactionTipsBottomSheet())
        }
    }

    private fun navToArc59SendSummaryFragment(payload: AssetSelectionOptInPayload) {
        nav(
            AssetSelectionFragmentDirections.actionAssetSelectionFragmentToArc59RequestOptInNavigation(
                Arc59SendSummaryNavArgs(
                    senderPublicKey = payload.senderAddress,
                    receiverPublicKey = payload.receiverAddress,
                    assetId = payload.assetId,
                    assetAmount = payload.assetAmount
                )
            )
        )
    }
}
