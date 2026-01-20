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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.LoadingDialogFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewEvent
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewState
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NameJointAccountFragment : DaggerBaseFragment(0), NameJointAccountScreenListener {

    private val viewModel: NameJointAccountViewModel by viewModels()
    private val args: NameJointAccountFragmentArgs by navArgs()
    private var loadingDialogFragment: LoadingDialogFragment? = null

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            NameJointAccountScreen(
                viewModel = viewModel,
                listener = this
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = viewModel.state,
            collection = ::handleViewState
        )
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = viewModel.viewEvent,
            collection = ::handleViewEvent
        )
    }

    private fun handleViewState(viewState: ViewState) {
        when (viewState) {
            is ViewState.Loading -> showLoadingDialog()
            is ViewState.Success -> dismissLoadingDialog()
            is ViewState.Idle, is ViewState.Error -> dismissLoadingDialog()
        }
    }

    private fun handleViewEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.AccountCreatedSuccessfully -> {
                dismissLoadingDialog()
                showSuccessMessage()
                popBackToAccounts()
            }
        }
    }

    private fun showSuccessMessage() {
        val mainActivity = activity as? MainActivity
        mainActivity?.showAlertSuccess(
            title = getString(R.string.account_has_been_added),
            description = null,
            tag = this::class.simpleName.orEmpty()
        )
    }

    private fun showLoadingDialog() {
        if (loadingDialogFragment == null) {
            loadingDialogFragment = LoadingDialogFragment.show(
                childFragmentManager = childFragmentManager,
                descriptionResId = R.string.creating_joint_account,
                isCancellable = false
            )
        }
    }

    private fun dismissLoadingDialog() {
        loadingDialogFragment?.dismissAllowingStateLoss()
        loadingDialogFragment = null
    }

    override fun onBackClick() {
        navBack()
    }

    override fun onFinishClick(accountName: String) {
        val participantAddresses = args.participantAddresses?.toList() ?: emptyList()
        viewModel.createJointAccount(
            accountName = accountName,
            threshold = args.threshold,
            participantAddresses = participantAddresses
        )
    }

    private fun popBackToAccounts() {
        findNavController().popBackStack(R.id.accountsFragment, false)
    }
}
