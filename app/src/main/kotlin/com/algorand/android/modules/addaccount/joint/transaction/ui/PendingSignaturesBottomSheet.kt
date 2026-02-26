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
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.JointAccountLedgerSignHelper
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewEvent
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.algorand.android.utils.showWithStateCheck
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingSignaturesBottomSheet : BaseBottomSheet(layoutResId = 0) {

    private val viewModel: JointAccountTransactionViewModel by viewModels()

    @Inject
    lateinit var ledgerSignHelper: JointAccountLedgerSignHelper

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener { shouldStopResources ->
        hideLedgerLoading()
        if (shouldStopResources) {
            ledgerSignHelper.cancel()
        }
    }

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
                onCancel = ::onCancel,
                onSignLedgerAccount = ::onSignLedgerAccount
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDismissBehavior()
        ledgerSignHelper.setup(viewLifecycleOwner.lifecycle)
        observeViewEvents()
        observeLedgerSignResult()
    }

    private fun onSignLedgerAccount(signer: JointAccountSignerItem) {
        viewModel.onSignLedgerAccount(signer)
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
                            Toast.makeText(
                                requireContext(),
                                getString(event.messageResId),
                                Toast.LENGTH_SHORT
                            ).show()
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

                        is ViewEvent.StartLedgerSigning -> {
                            ledgerSignHelper.signWithLedger(
                                signRequestId = event.data.signRequestId,
                                accountAddress = event.data.accountAddress,
                                rawTransactionsBase64 = event.data.rawTransactions,
                                ledgerBluetoothAddress = event.data.ledgerBluetoothAddress,
                                ledgerAccountIndex = event.data.ledgerAccountIndex,
                                accountAuthAddress = event.data.accountAuthAddress,
                                isRekeyedToAnotherAccount = event.data.isRekeyedToAnotherAccount
                            )
                        }

                        else -> { /* Other events handled elsewhere */
                        }
                    }
                }
            }
        }
    }

    private fun observeLedgerSignResult() {
        viewLifecycleOwner.collectOnLifecycle(
            flow = ledgerSignHelper.signResultFlow,
            collection = ::handleLedgerSignResult,
            state = Lifecycle.State.STARTED
        )
    }

    private fun handleLedgerSignResult(result: JointAccountLedgerSignHelper.LedgerSignResult) {
        when (result) {
            is JointAccountLedgerSignHelper.LedgerSignResult.Scanning -> {
                showLedgerLoading(getString(R.string.searching_for_ledger))
            }

            is JointAccountLedgerSignHelper.LedgerSignResult.WaitingForApproval -> {
                showLedgerLoading(
                    result.bluetoothName ?: getString(R.string.ledger),
                    result.currentTransactionIndex,
                    result.totalTransactionCount
                )
            }

            is JointAccountLedgerSignHelper.LedgerSignResult.Submitting -> Unit
            is JointAccountLedgerSignHelper.LedgerSignResult.Success -> {
                hideLedgerLoading()
                ledgerSignHelper.resetState()
                viewModel.onLedgerSignSuccess()
                Toast.makeText(
                    requireContext(),
                    R.string.signature_submitted_successfully,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is JointAccountLedgerSignHelper.LedgerSignResult.Error -> {
                hideLedgerLoading()
                ledgerSignHelper.resetState()
                viewModel.onLedgerSignError(result.errorMessageResId)
            }

            is JointAccountLedgerSignHelper.LedgerSignResult.Cancelled -> {
                hideLedgerLoading()
                ledgerSignHelper.resetState()
            }

            is JointAccountLedgerSignHelper.LedgerSignResult.Idle -> Unit
        }
    }

    private fun showLedgerLoading(
        ledgerName: String,
        currentTransactionIndex: Int? = null,
        totalTransactionCount: Int? = null
    ) {
        val isTransactionIndicatorVisible =
            currentTransactionIndex != null && totalTransactionCount != null

        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerName,
                listener = ledgerLoadingDialogListener,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount,
                isTransactionIndicatorVisible = isTransactionIndicatorVisible
            )
            ledgerLoadingDialog?.showWithStateCheck(childFragmentManager, LEDGER_LOADING_TAG)
        } else {
            currentTransactionIndex?.let {
                ledgerLoadingDialog?.updateTransactionIndicator(it)
            }
        }
    }

    private fun hideLedgerLoading() {
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
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

    override fun onDestroyView() {
        super.onDestroyView()
        hideLedgerLoading()
    }

    companion object {
        const val TAG = "PendingSignaturesBottomSheet"
        private const val IS_DISMISSABLE_KEY = "isDismissable"
        private const val LEDGER_LOADING_TAG = "ledger_loading"
    }
}
