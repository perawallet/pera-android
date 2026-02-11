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

package com.algorand.android.modules.inbox.jointaccountinvitation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationDetailViewState
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JointAccountInvitationDetailFragment : DaggerBaseFragment(0),
    JointAccountInvitationDetailScreenListener {

    private val viewModel: JointAccountInvitationDetailViewModel by viewModels()

    private val args: JointAccountInvitationDetailFragmentArgs by navArgs()

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            val viewState by viewModel.state.collectAsState()

            when (val state = viewState) {
                is JointAccountInvitationDetailViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        PeraCircularProgressIndicator()
                    }
                }

                is JointAccountInvitationDetailViewState.Content -> {
                    JointAccountInvitationDetailScreen(
                        invitation = state.invitation,
                        accountDisplayNames = state.accountDisplayNames,
                        accountIcons = state.accountIcons,
                        listener = this@JointAccountInvitationDetailFragment
                    )
                }
            }
        }
    }

    override fun onBackClick() {
        navBack()
    }

    override fun onAcceptClick() {
        viewModel.onAcceptClick()
    }

    override fun onRejectClick() {
        viewModel.onRejectClick()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = viewModel.viewEvent,
            collection = { event ->
                when (event) {
                    is JointAccountInvitationDetailViewModel.ViewEvent.InvitationIgnored -> {
                        showGlobalError(getString(R.string.invitation_ignored))
                        navBack()
                    }

                    is JointAccountInvitationDetailViewModel.ViewEvent.ShowError -> {
                        showGlobalError(getString(R.string.an_error_occurred))
                        navBack()
                    }

                    is JointAccountInvitationDetailViewModel.ViewEvent.NavigateToNameJointAccount -> {
                        showAlertSuccess(getString(R.string.joint_account_invitation_accepted))
                        nav(
                            HomeNavigationDirections.actionGlobalToNameJointAccountFragment(
                                threshold = event.threshold,
                                participantAddresses = event.participantAddresses.toTypedArray()
                            )
                        )
                    }
                }
            }
        )
    }

    override fun onCopyAddress(address: String) {
        onAccountAddressCopied(address)
    }
}
