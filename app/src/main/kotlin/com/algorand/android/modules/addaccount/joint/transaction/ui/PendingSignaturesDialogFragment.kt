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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewEvent
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewState
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingSignaturesDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: JointAccountTransactionViewModel by viewModels()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme_Primary

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewEvents()
    }

    private fun observeViewEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewEvent.collect { event ->
                    when (event) {
                        is ViewEvent.ShowError -> {
                            Toast.makeText(requireContext(), event.messageResId, Toast.LENGTH_SHORT).show()
                        }
                        is ViewEvent.NavigateBack -> dismiss()
                        else -> { /* Other events handled elsewhere */ }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            val viewState by viewModel.state.collectAsStateWithLifecycle()

            when (val state = viewState) {
                is ViewState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = PeraTheme.colors.background.primary,
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PeraCircularProgressIndicator()
                    }
                }
                is ViewState.Content -> {
                    PendingSignaturesContent(
                        transactionPreview = state.preview,
                        onCancel = { viewModel.declineSignRequest() },
                        onCloseForNow = { dismiss() },
                        onCloseCompleted = {
                            dismiss()
                            activity?.let { activity ->
                                val navController = androidx.navigation.Navigation.findNavController(
                                    activity,
                                    R.id.navigationHostFragment
                                )
                                navController.popBackStack(R.id.accountsFragment, false)
                            }
                        }
                    )
                }
                is ViewState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = PeraTheme.colors.background.primary,
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PeraCircularProgressIndicator()
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet = findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                bottomSheet?.let {
                    it.setBackgroundResource(R.drawable.bottom_sheet_dialog_fragment_primary_background)
                    BottomSheetBehavior.from(it).apply {
                        skipCollapsed = true
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "PendingSignaturesDialogFragment"
        const val SIGN_REQUEST_ID_KEY = "signRequestId"

        fun newInstance(signRequestId: String): PendingSignaturesDialogFragment {
            return PendingSignaturesDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(SIGN_REQUEST_ID_KEY, signRequestId)
                }
            }
        }
    }
}
