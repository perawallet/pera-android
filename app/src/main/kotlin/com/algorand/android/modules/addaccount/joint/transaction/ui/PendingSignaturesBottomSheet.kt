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
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewEvent
import com.algorand.android.ui.compose.extensions.createComposeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingSignaturesBottomSheet : BaseBottomSheet(layoutResId = 0) {

    private val viewModel: JointAccountTransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            PendingSignaturesBottomSheetScreen(
                viewModel = viewModel,
                onCloseCompleted = ::onCloseCompleted,
                onCloseForNow = ::onCloseForNow,
                onCancel = ::onCancel
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDismissBehavior()
        observeViewEvents()
    }

    private val isDismissable: Boolean
        get() = arguments?.getBoolean(IS_DISMISSABLE_KEY, true) ?: true

    private fun setupDismissBehavior() {
        setDraggableEnabled(isDismissable)
        isCancelable = isDismissable
    }

    private fun observeViewEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewEvent.collect { event ->
                    when (event) {
                        is ViewEvent.ShowError -> {
                            Toast.makeText(requireContext(), event.messageResId, Toast.LENGTH_SHORT).show()
                        }

                        is ViewEvent.ShowSuccessAndNavigateBack -> {
                            (activity as? MainActivity)?.showAlertSuccess(
                                title = getString(event.messageResId),
                                description = null,
                                tag = TAG
                            )
                            dismiss()
                        }

                        is ViewEvent.NavigateBack -> dismiss()
                        else -> { /* Other events handled elsewhere */
                        }
                    }
                }
            }
        }
    }

    private fun onCloseCompleted() {
        popToInboxOrAccounts()
    }

    private fun popToInboxOrAccounts() {
        dismiss()
        activity?.let { activity ->
            val navController = androidx.navigation.Navigation.findNavController(
                activity,
                R.id.navigationHostFragment
            )
            val poppedToInbox = navController.popBackStack(R.id.inboxFragment, false)
            if (!poppedToInbox) {
                navController.popBackStack(R.id.accountsFragment, false)
            }
        }
    }

    private fun onCloseForNow() {
        if (isDismissable) {
            dismiss()
        } else {
            popToInboxOrAccounts()
        }
    }

    private fun onCancel() {
        viewModel.declineSignRequest()
    }

    companion object {
        const val TAG = "PendingSignaturesBottomSheet"
        private const val IS_DISMISSABLE_KEY = "isDismissable"
    }
}
