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

package com.algorand.android.modules.addaccount.joint.transaction.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewEvent
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.copyToClipboard
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JointAccountSignRequestFragment : DaggerBaseFragment(0),
    JointAccountSignRequestScreenListener {

    override val fragmentConfiguration = FragmentConfiguration()

    private val viewModel: JointAccountTransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            JointAccountSignRequestScreen(
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
            flow = viewModel.viewEvent,
            collection = ::handleViewEvent
        )
    }

    private fun handleViewEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.NavigateBack -> navBack()
            is ViewEvent.ShowSuccessAndNavigateBack -> {
                showAlertSuccess(title = getString(event.messageResId), tag = baseActivityTag)
                navBack()
            }

            is ViewEvent.ShowError -> showGlobalError(getString(event.messageResId))
            is ViewEvent.ShowPendingSignaturesBottomSheet -> {
                navToPendingSignaturesBottomSheet(event.signRequestId, event.isDismissable)
            }

            is ViewEvent.StartLedgerSigning -> {
                // Handled by PendingSignaturesBottomSheet only
            }

            is ViewEvent.CopyAddress -> {
                context?.copyToClipboard(event.address)
            }
        }
    }

    override fun onCloseClick() {
        navBack()
    }

    override fun onCopyAddressClick() {
        viewModel.onCopyAddressClick()
    }

    override fun onDeclineClick() {
        viewModel.declineSignRequest()
    }

    override fun onShowTransactionDetailsClick() {
        viewModel.onShowTransactionDetailsClick()
    }

    override fun onNavigateToHome() {
        navBack()
    }

    private fun navToPendingSignaturesBottomSheet(signRequestId: String, isDismissable: Boolean) {
        nav(HomeNavigationDirections.actionGlobalToPendingSignaturesBottomSheet(signRequestId, isDismissable))
    }
}
