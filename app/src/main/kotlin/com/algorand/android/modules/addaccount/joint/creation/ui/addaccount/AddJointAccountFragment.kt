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

package com.algorand.android.modules.addaccount.joint.creation.ui.addaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.AddJointAccountQrScannerFragment.Companion.ACCOUNT_ADDRESS_QR_SCAN_RESULT_KEY
import com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.viewmodel.AddJointAccountViewModel
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.hideKeyboard
import com.algorand.android.utils.setFragmentNavigationResult
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useSavedStateValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddJointAccountFragment : DaggerBaseFragment(0), AddJointAccountScreenListener {

    private val viewModel: AddJointAccountViewModel by viewModels()

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            AddJointAccountScreen(
                viewModel = viewModel,
                listener = this
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initSavedStateListener()
    }

    private fun initSavedStateListener() {
        startSavedStateListener(R.id.addJointAccountFragment) {
            useSavedStateValue<String>(ACCOUNT_ADDRESS_QR_SCAN_RESULT_KEY) { address ->
                viewModel.onSearchQueryUpdate(address)
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = viewModel.viewEvent,
            collection = { event ->
                when (event) {
                    is AddJointAccountViewModel.ViewEvent.NavigateBackWithSelectedAccount -> {
                        setResultAndNavigateBack(event.account)
                    }

                    is AddJointAccountViewModel.ViewEvent.ShowError -> {
                        showGlobalError(getString(R.string.an_error_occurred))
                    }
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.resetSearchQuery()
    }

    override fun onBackClick() {
        viewModel.resetSearchQuery()
        navBack()
    }

    override fun onAccountSelected(address: String) {
        val selectedAccount = viewModel.createSelectedAccountFromItem(address)
        if (selectedAccount != null) {
            setResultAndNavigateBack(selectedAccount)
        } else {
            showGlobalError(getString(R.string.an_error_occurred))
        }
    }

    override fun onExternalAddressSelected(address: String) {
        viewModel.onExternalAddressSelected(address)
    }

    override fun onNfdSelected(address: String) {
        viewModel.onNfdSelected(address)
    }

    override fun onQrScanClick() {
        view?.hideKeyboard()
        nav(AddJointAccountFragmentDirections.actionAddJointAccountFragmentToAddJointAccountQrScannerFragment())
    }

    private fun setResultAndNavigateBack(selectedAccount: SelectedJointAccountItem) {
        setFragmentNavigationResult(RESULT_SELECTED_ACCOUNT, selectedAccount)
        navBack()
    }

    companion object {
        const val RESULT_SELECTED_ACCOUNT = "result_selected_account"
    }
}
