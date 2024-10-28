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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.TransactionBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.databinding.FragmentAssetInboxOneAccountBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountPreview
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs
import com.algorand.android.utils.BaseCustomDividerItemDecoration
import com.algorand.android.utils.addCustomDivider
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetInboxOneAccountFragment :
    TransactionBaseFragment(R.layout.fragment_asset_inbox_one_account) {

    private val infoButton by lazy { IconButton(R.drawable.ic_info, onClick = ::onInfoButtonClick) }

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.asset_transfer_requests,
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow,
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val binding by viewBinding(FragmentAssetInboxOneAccountBinding::bind)

    private val assetInboxOneAccountViewModel: AssetInboxOneAccountViewModel by viewModels()

    private val inboxAssetSelectionListener = object : InboxAsaSelectionAdapter.Listener {
        override fun onAsaItemClick(asaPreview: AsaPreview) {
            onAssetClicked(asaPreview)
        }
    }

    private val assetAdapter = InboxAsaSelectionAdapter(inboxAssetSelectionListener)

    private val viewStateCollector: suspend (AssetInboxOneAccountPreview) -> Unit = { preview ->
        initPreview(preview)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initObservers()
        initUi()
        assetInboxOneAccountViewModel.initializePreview()
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = assetInboxOneAccountViewModel.viewStateFlow,
            collection = viewStateCollector
        )
    }

    private fun initPreview(preview: AssetInboxOneAccountPreview) {
        preview.showError?.consume()?.let { error ->
            context?.let { showGlobalError(error.parseError(it), tag = baseActivityTag) }
        }
        if (preview.isLoading) showLoading() else hideLoading()
        if (preview.isEmptyStateVisible) showEmptyState() else hideEmptyState()
        preview.onNavBack?.consume()?.let { navBack() }
        assetAdapter.submitList(preview.asaPreviewList)
    }

    private fun initUi() {
        binding.assetsRecyclerView.apply {
            adapter = assetAdapter
            addCustomDivider(
                drawableResId = R.drawable.horizontal_divider_80_24dp,
                showLast = false,
                divider = BaseCustomDividerItemDecoration()
            )
        }
    }

    private fun setupToolbar() {
        getAppToolbar()?.run {
            setEndButton(button = infoButton)
        }
    }

    private fun onAssetClicked(asaPreview: AsaPreview) {
        navToArc59ReceiveDetailFragment(
            assetInboxOneAccountViewModel.getArc59ReceiveDetailNavArgs(
                asaPreview
            )
        )
    }

    private fun onInfoButtonClick() {
        navToAssetInboxInfoNavigation()
    }

    private fun navToAssetInboxInfoNavigation() {
        nav(
            AssetInboxOneAccountFragmentDirections
                .actionAssetInboxOneAccountFragmentToAssetInboxInfoNavigation()
        )
    }

    private fun navToArc59ReceiveDetailFragment(arc59ReceiveDetailNavArgs: Arc59ReceiveDetailNavArgs) {
        nav(
            AssetInboxOneAccountFragmentDirections
                .actionAssetInboxOneAccountFragmentToArc59ReceiveDetailFragment(
                    arc59ReceiveDetailNavArgs
                )
        )
    }

    private fun showLoading() {
        binding.progressbar.root.show()
    }

    private fun hideLoading() {
        binding.progressbar.root.hide()
    }

    private fun showEmptyState() {
        binding.emptyStateGroup.show()
    }

    private fun hideEmptyState() {
        binding.emptyStateGroup.hide()
    }
}
