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

package com.algorand.android.modules.addaccount.joint.creation.ui.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.AddJointAccountFragment
import com.algorand.android.modules.addaccount.joint.creation.ui.createaccount.viewmodel.CreateJointAccountViewModel
import com.algorand.android.modules.addaccount.joint.creation.ui.editname.EditAccountNameFragment
import com.algorand.android.ui.compose.extensions.createComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateJointAccountFragment : DaggerBaseFragment(0), CreateJointAccountScreenListener {

    private val viewModel: CreateJointAccountViewModel by viewModels()

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackNavigation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            CreateJointAccountScreen(
                viewModel = viewModel,
                listener = this
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        observeResults()
    }

    private fun observeResults() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle

        savedStateHandle?.getLiveData<SelectedJointAccountItem>(AddJointAccountFragment.RESULT_SELECTED_ACCOUNT)
            ?.observe(viewLifecycleOwner) { selectedAccount ->
                selectedAccount?.let {
                    val wasAdded = viewModel.addSelectedAccount(it)
                    if (!wasAdded) {
                        showGlobalError(getString(R.string.this_account_already_exists))
                    }
                    savedStateHandle.remove<SelectedJointAccountItem>(AddJointAccountFragment.RESULT_SELECTED_ACCOUNT)
                }
            }

        savedStateHandle?.getLiveData<String>(EditAccountNameFragment.RESULT_UPDATED_NAME)
            ?.observe(viewLifecycleOwner) { updatedName ->
                val address = savedStateHandle.get<String>(EditAccountNameFragment.RESULT_ADDRESS)
                if (updatedName != null && address != null) {
                    viewModel.updateAccountName(address, updatedName)
                    savedStateHandle.remove<String>(EditAccountNameFragment.RESULT_UPDATED_NAME)
                    savedStateHandle.remove<String>(EditAccountNameFragment.RESULT_ADDRESS)
                }
            }

        savedStateHandle?.getLiveData<String>(EditAccountNameFragment.RESULT_REMOVED_ADDRESS)
            ?.observe(viewLifecycleOwner) { removedAddress ->
                removedAddress?.let {
                    viewModel.removeSelectedAccount(it)
                    savedStateHandle.remove<String>(EditAccountNameFragment.RESULT_REMOVED_ADDRESS)
                }
            }
    }

    override fun onBackClick() {
        handleBackNavigation()
    }

    private fun handleBackNavigation() {
        navBack()
    }

    override fun onAddAccountClick() {
        navToAddJointAccountFragment()
    }

    override fun onEditAccountClick(address: String) {
        navToEditAccountNameFragment(address)
    }

    override fun onContinueClick() {
        navToSetThresholdFragment()
    }

    private fun navToAddJointAccountFragment() {
        nav(
            CreateJointAccountFragmentDirections
                .actionCreateJointAccountFragmentToAddJointAccountFragment()
        )
    }

    private fun navToEditAccountNameFragment(accountAddress: String) {
        nav(
            CreateJointAccountFragmentDirections
                .actionCreateJointAccountFragmentToEditAccountNameFragment(
                    accountAddress = accountAddress
                )
        )
    }

    private fun navToSetThresholdFragment() {
        nav(
            CreateJointAccountFragmentDirections
                .actionCreateJointAccountFragmentToSetThresholdFragment(
                    participantAddresses = viewModel.getParticipantAddresses()
                )
        )
    }
}
