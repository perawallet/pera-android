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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.TransactionBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.databinding.FragmentAssetInboxAllAccountsBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountNavArgs
import com.algorand.android.utils.BaseCustomDividerItemDecoration
import com.algorand.android.utils.addCustomDivider
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetInboxAllAccountsFragment :
    TransactionBaseFragment(R.layout.fragment_asset_inbox_all_accounts) {

    private val infoButton by lazy { IconButton(R.drawable.ic_info, onClick = ::onInfoButtonClick) }

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.asset_transfer_requests,
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow,
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val binding by viewBinding(FragmentAssetInboxAllAccountsBinding::bind)

    private val assetInboxAllAccountsViewModel: AssetInboxAllAccountsViewModel by viewModels()

    private val inboxAccountSelectionListener = object : InboxAccountSelectionAdapter.Listener {
        override fun onAccountItemClick(publicKey: String) {
            onAccountClicked(publicKey)
        }
    }

    protected val accountAdapter = InboxAccountSelectionAdapter(inboxAccountSelectionListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initObservers()
        initUi()
        assetInboxAllAccountsViewModel.initializePreview()
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = assetInboxAllAccountsViewModel.viewStateFlow,
            collection = viewStateCollector
        )
    }

    private val viewStateCollector: suspend (AssetInboxAllAccountsPreview) -> Unit = { preview ->
        initPreview(preview)
    }

    private fun initPreview(preview: AssetInboxAllAccountsPreview) {
        preview.showError?.consume()?.let { error ->
            context?.let { showGlobalError(error.parseError(it), tag = baseActivityTag) }
        }
        if (preview.isLoading) showLoading() else hideLoading()
        if (preview.isEmptyStateVisible) showEmptyState() else hideEmptyState()
        accountAdapter.submitList(preview.assetInboxAllAccountsWithAccountList)
    }

    private fun initUi() {
        binding.accountsRecyclerView.apply {
            adapter = accountAdapter
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

    private fun onAccountClicked(publicKey: String) {
        navToAssetInboxOneAccountNavigation(AssetInboxOneAccountNavArgs(publicKey))
    }

    private fun onInfoButtonClick() {
        navToAssetInboxInfoNavigation()
    }

    private fun navToAssetInboxInfoNavigation() {
        nav(
            AssetInboxAllAccountsFragmentDirections
                .actionAssetInboxAllAccountsFragmentToAssetInboxInfoNavigation()
        )
    }

    private fun navToAssetInboxOneAccountNavigation(assetInboxOneAccountNavArgs: AssetInboxOneAccountNavArgs) {
        nav(
            AssetInboxAllAccountsFragmentDirections
                .actionAssetInboxAllAccountsFragmentToAssetInboxOneAccountNavigation(
                    assetInboxOneAccountNavArgs
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
