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

package com.algorand.android.ui.rekeyedaccounts.view

import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentRekeyedAccountSelectionBinding
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AccountItem
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel.ViewEvent.NavigateToNextScreen
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel.ViewEvent.ShowAccountLimitError
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel.ViewState.Content
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel.ViewState.Idle
import com.algorand.android.utils.extensions.collectLatestOnLifecycle

class RekeyedAccountSelectionFragmentDelegate(
    private val fragment: BaseFragment,
    private val listener: RekeyedAccountSelectionListener,
    private val viewModel: RekeyedAccountSelectionViewModel,
    private val binding: FragmentRekeyedAccountSelectionBinding
) {

    private val rekeyedAccountSelectionAdapterListener = object : RekeyedAccountSelectionAdapter.Listener {

        override fun onAccountItemClick(accountItem: AccountItem) {
            viewModel.selectAccount(accountItem)
        }

        override fun onAccountItemInformationClick(accountAddress: String) {
            listener.navToAccountInformationBottomSheet(accountAddress)
        }
    }

    private val viewStateCollector: suspend (RekeyedAccountSelectionViewModel.ViewState) -> Unit = { state ->
        when (state) {
            Idle -> Unit
            is Content -> updateContentState(state)
        }
    }

    private val viewEventCollector: suspend (RekeyedAccountSelectionViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            NavigateToNextScreen -> listener.navToNextScreen()
            ShowAccountLimitError -> fragment.showMaxAccountLimitExceededError()
        }
    }

    private val rekeyedAccountSelectionAdapter = RekeyedAccountSelectionAdapter(rekeyedAccountSelectionAdapterListener)

    fun init() {
        initObserver()
        initUi()
    }

    private fun initObserver() {
        fragment.collectLatestOnLifecycle(viewModel.state, viewStateCollector)
        fragment.collectLatestOnLifecycle(viewModel.viewEvent, viewEventCollector)
    }

    private fun initUi() {
        with(binding) {
            rekeyedAccountSelectionRecyclerView.adapter = rekeyedAccountSelectionAdapter
            primaryActionButton.setOnClickListener { viewModel.addSelectedAccounts() }
            secondaryActionButton.setOnClickListener { listener.navToNextScreen() }
        }
    }

    private fun updateContentState(state: Content) {
        rekeyedAccountSelectionAdapter.submitList(state.selectionItems)
        val textResId = if (state.isAddButtonEnabled) R.string.add_rekeyed_accounts else R.string.choose_accounts_to_add
        binding.primaryActionButton.apply {
            isEnabled = state.isAddButtonEnabled
            setText(textResId)
        }
    }

    interface RekeyedAccountSelectionListener {
        fun navToNextScreen()
        fun navToAccountInformationBottomSheet(address: String)
    }
}
