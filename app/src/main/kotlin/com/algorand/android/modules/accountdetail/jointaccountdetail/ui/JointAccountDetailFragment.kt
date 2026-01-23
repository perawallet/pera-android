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

package com.algorand.android.modules.accountdetail.jointaccountdetail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel.ViewEvent
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JointAccountDetailFragment : DaggerBaseFragment(0) {

    private val viewModel: JointAccountDetailViewModel by viewModels()

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            val viewState by viewModel.state.collectAsStateWithLifecycle()

            JointAccountDetailScreen(
                viewState = viewState,
                accountAddress = viewModel.accountAddress,
                onEvent = ::handleEvent
            )
        }
    }

    private fun handleEvent(event: JointAccountDetailEvent) {
        when (event) {
            is JointAccountDetailEvent.BackClick -> navBack()
            is JointAccountDetailEvent.EditAddressClick -> viewModel.onEditContactClick(event.address)
            is JointAccountDetailEvent.CopyAddressClick -> onAccountAddressCopied(event.address)
            is JointAccountDetailEvent.IgnoreClick -> viewModel.onIgnoreClick()
            is JointAccountDetailEvent.AddClick -> viewModel.onAddClick()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        // Refresh participants list to reflect any changes made in EditContactFragment
        viewModel.refreshParticipants()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = viewModel.viewEvent,
            collection = { event ->
                when (event) {
                    is ViewEvent.NavigateBack -> navBack()
                    is ViewEvent.NavigateToNameJointAccount -> navigateToNameJointAccount(event)
                    is ViewEvent.NavigateToEditContact -> navigateToEditContact(event)
                }
            }
        )
    }

    private fun navigateToNameJointAccount(event: ViewEvent.NavigateToNameJointAccount) {
        nav(
            HomeNavigationDirections.actionGlobalToNameJointAccountFragment(
                threshold = event.threshold,
                participantAddresses = event.participantAddresses.toTypedArray()
            )
        )
    }

    private fun navigateToEditContact(event: ViewEvent.NavigateToEditContact) {
        nav(
            HomeNavigationDirections.actionGlobalEditContactFragment(
                contactName = event.contactName,
                contactPublicKey = event.contactPublicKey,
                contactDatabaseId = event.contactDatabaseId,
                contactProfileImageUri = event.contactProfileImageUri
            )
        )
    }
}
