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
import com.algorand.android.modules.addaccount.joint.creation.ui.editname.EditAccountNameFragment.Companion.RESULT_UPDATED_NAME
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.useFragmentResultListenerValue
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
                viewModel = viewModel
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initResultListeners()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = viewModel.viewEvent,
            collection = { event ->
                when (event) {
                    is ViewEvent.NavigateBack -> navBack()
                    is ViewEvent.NavigateToNameJointAccount -> navigateToNameJointAccount(event)
                    is ViewEvent.NavigateToEditAddress -> navigateToEditAddress(event)
                    is ViewEvent.ShowAddressCopied -> onAccountAddressCopied(event.address)
                }
            }
        )
    }

    private fun initResultListeners() {
        useFragmentResultListenerValue<String>(RESULT_UPDATED_NAME) { newName ->
            viewModel.onParticipantNameUpdated(newName)
        }
    }

    private fun navigateToNameJointAccount(event: ViewEvent.NavigateToNameJointAccount) {
        nav(
            HomeNavigationDirections.actionGlobalToNameJointAccountFragment(
                threshold = event.threshold,
                participantAddresses = event.participantAddresses.toTypedArray()
            )
        )
    }

    private fun navigateToEditAddress(event: ViewEvent.NavigateToEditAddress) {
        nav(
            JointAccountDetailFragmentDirections
                .actionJointAccountDetailFragmentToEditParticipantNameFragment(event.address)
        )
    }
}
