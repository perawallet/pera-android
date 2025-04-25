/*
 * Copyright 2025 Pera Wallet, LDA
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

package com.algorand.android.ui.send.senderaccount

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.transaction.TransactionSignBaseFragment
import com.algorand.android.databinding.FragmentSenderAccountSelectionBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.SenderAccountSelectionPreview
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.accountselection.AccountSelectionAdapter
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SenderAccountSelectionFragment : TransactionSignBaseFragment(R.layout.fragment_sender_account_selection) {

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.select_account,
        startIconResId = R.drawable.ic_close,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val senderAccountSelectionViewModel: SenderAccountSelectionViewModel by viewModels()

    private val binding by viewBinding(FragmentSenderAccountSelectionBinding::bind)

    private val listener = object : AccountSelectionAdapter.Listener {
        override fun onAccountItemClick(publicKey: String) {
            senderAccountSelectionViewModel.fetchSenderAccountInformation(publicKey)
        }
    }

    private val senderAccountSelectionAdapter = AccountSelectionAdapter(listener)

    private val senderAccountSelectionPreviewCollector: suspend (SenderAccountSelectionPreview) -> Unit = {
        updateUiWithPreview(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showTransactionTipsIfNeed()
        initObservers()
        binding.accountsToSendRecyclerView.adapter = senderAccountSelectionAdapter
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = senderAccountSelectionViewModel.senderAccountSelectionPreviewFlow,
            collection = senderAccountSelectionPreviewCollector
        )
    }

    private fun updateUiWithPreview(preview: SenderAccountSelectionPreview) {
        with(preview) {
            binding.progressBar.root.isVisible = isLoading
            senderAccountSelectionAdapter.submitList(accountList)
            binding.screenStateView.isVisible = isEmptyStateVisible

            senderAccountInformationSuccessEvent?.consume()?.let {
                senderAccountSelectionViewModel.handleNextNavigation(it)
            }
            senderAccountInformationErrorEvent?.consume()?.let { handleError(it.getAsResourceError(), binding.root) }
            navigateToDestination?.consume()?.let { nav(it) }
        }
    }

    private fun showTransactionTipsIfNeed() {
        if (senderAccountSelectionViewModel.shouldShowTransactionTips()) {
            nav(
                SenderAccountSelectionFragmentDirections
                    .actionSenderAccountSelectionFragmentToTransactionTipsBottomSheet()
            )
        }
    }
}
