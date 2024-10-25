/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.MainNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.databinding.FragmentArc59ReceiveDetailBinding
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ConfirmationBottomSheetParameters
import com.algorand.android.models.ConfirmationBottomSheetResult
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailPreview
import com.algorand.android.modules.assetinbox.detail.transactiondetail.Arc59TransactionDetailFragment
import com.algorand.android.modules.assetinbox.detail.transactiondetail.Arc59TransactionDetailViewModel.Companion.ARC59_TRANSACTION_DETAIL_NAV_ARGS
import com.algorand.android.modules.assetinbox.detail.transactiondetail.model.Arc59TransactionDetailArgs
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.utils.BaseDoubleButtonBottomSheet.Companion.RESULT_KEY
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Arc59ReceiveDetailFragment : BaseFragment(R.layout.fragment_arc59_receive_detail) {

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.asset_transfer_request,
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow,
    )

    private val binding by viewBinding(FragmentArc59ReceiveDetailBinding::bind)

    private val viewModel by viewModels<Arc59ReceiveDetailViewModel>()

    private lateinit var receiveDetailNavController: NavController

    override val fragmentConfiguration: FragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val previewFlowObserver: suspend (Arc59ReceiveDetailPreview) -> Unit = { preview ->
        initPreview(preview)
    }

    @Inject
    lateinit var arc59ClaimRejectTransactionSignManager: Arc59ClaimRejectTransactionSignManager

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener {
        ledgerLoadingDialog = null
        arc59ClaimRejectTransactionSignManager.stopAllResources()
    }

    private val externalTransactionSignManagerCollector: suspend (ExternalTransactionSignResult) -> Unit = {
        if (it !is ExternalTransactionSignResult.Loading) hideLoading()
        when (it) {
            is ExternalTransactionSignResult.Success<*> -> sendSignedTransactions(it.signedTransaction)
            is ExternalTransactionSignResult.Error -> showTransactionSignResultError(it)
            ExternalTransactionSignResult.LedgerScanFailed -> showLedgerNotFoundDialog()
            is ExternalTransactionSignResult.LedgerWaitingForApproval -> showLedgerWaitingForApprovalBottomSheet(it)
            ExternalTransactionSignResult.Loading -> showLoading()
            ExternalTransactionSignResult.NotInitialized -> Unit
            is ExternalTransactionSignResult.TransactionCancelled -> showTransactionCancelledError(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        arc59ClaimRejectTransactionSignManager.setup(viewLifecycleOwner.lifecycle)
    }

    override fun onResume() {
        super.onResume()
        startSavedStateListener(R.id.arc59ReceiveDetailFragment) {
            useSavedStateValue<ConfirmationBottomSheetResult>(RESULT_KEY) {
                if (it.isAccepted) viewModel.rejectTransaction()
            }
        }
    }

    private fun initUi() {
        initNavController()
        with(binding) {
            confirmButton.setOnClickListener {
                viewModel.claimTransaction()
            }
            declineButton.setOnClickListener { onRejectClick() }
        }
    }

    private fun onRejectClick() {
        navigateToConfirmationBottomSheet()
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = viewModel.previewFlow,
            collection = previewFlowObserver
        )
        collectLatestOnLifecycle(
            flow = arc59ClaimRejectTransactionSignManager.arc59ClaimRejectTransactionSignResultFlow,
            collection = externalTransactionSignManagerCollector
        )
    }

    private fun initNavController() {
        val fragmentContainer = childFragmentManager.findFragmentById(binding.requestDetailNavigationHostFragment.id)
        receiveDetailNavController = (fragmentContainer as NavHostFragment).navController
    }

    private fun initPreview(preview: Arc59ReceiveDetailPreview) {
        initTransactionDetailFragment(preview.arc59TransactionDetailArgs)
        preview.claimTransaction?.consume()?.let { arc59ClaimRejectTransactionSignManager.signTransaction(it) }
        preview.rejectTransaction?.consume()?.let { arc59ClaimRejectTransactionSignManager.signTransaction(it) }
        preview.showError?.consume()?.let { error ->
            context?.let {
                showGlobalError(
                    error.parseError(it), tag =
                    baseActivityTag
                )
            }
        }
        if (preview.isLoading) showLoading() else hideLoading()
        preview.onTransactionSendSuccessfully?.consume()?.let { transactionId ->
            navToTransactionConfirmationNavigation(transactionId)
        }
    }

    private fun initTransactionDetailFragment(args: Arc59TransactionDetailArgs) {
        val navArgs = Bundle().apply {
            putParcelable(ARC59_TRANSACTION_DETAIL_NAV_ARGS, args)
        }
        parentFragmentManager.beginTransaction()
            .add(binding.requestDetailNavigationHostFragment.id, Arc59TransactionDetailFragment::class.java, navArgs)
            .commit()
    }

    private fun navigateToConfirmationBottomSheet() {
        val parameters = ConfirmationBottomSheetParameters(
            confirmationIdentifier = REJECT_CONFIRMATION_ID,
            titleResId = R.string.rejecting_requests,
            descriptionText = getString(R.string.if_you_reject_this_request, viewModel.getGainOnRejectAmount()),
            confirmButtonTextResId = R.string.reject,
            rejectButtonTextResId = R.string.cancel
        )
        nav(MainNavigationDirections.actionGlobalConfirmationBottomSheet(parameters))
    }

    private fun showLoading() {
        binding.fullScreenProgressBar.root.show()
    }

    private fun hideLoading() {
        binding.fullScreenProgressBar.root.hide()
    }

    private fun sendSignedTransactions(signedTransaction: List<Any?>) {
        dismissLedgerDialog()
        viewModel.sendSignedTransaction(signedTransaction)
    }

    private fun dismissLedgerDialog() {
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun showTransactionSignResultError(error: ExternalTransactionSignResult.Error) {
        dismissLedgerDialog()
        context?.run {
            val (title, description) = error.getMessage(this)
            showGlobalError(description, title)
        }
    }

    private fun showLedgerNotFoundDialog() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun showLedgerWaitingForApprovalBottomSheet(
        ledgerPayload: ExternalTransactionSignResult.LedgerWaitingForApproval
    ) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerPayload.ledgerName,
                listener = ledgerLoadingDialogListener,
                currentTransactionIndex = ledgerPayload.currentTransactionIndex,
                totalTransactionCount = ledgerPayload.totalTransactionCount,
                isTransactionIndicatorVisible = ledgerPayload.isTransactionIndicatorVisible
            )
            ledgerLoadingDialog?.showWithStateCheck(childFragmentManager, ledgerPayload.ledgerName.orEmpty())
        } else {
            ledgerLoadingDialog?.updateTransactionIndicator(ledgerPayload.currentTransactionIndex)
        }
    }

    private fun showTransactionCancelledError(result: ExternalTransactionSignResult.TransactionCancelled) {
        dismissLedgerDialog()
        val annotatedString = (result.error as? ExternalTransactionSignResult.Error.Defined)?.description
            ?: AnnotatedString(R.string.an_error_occured)
        context?.getXmlStyledString(annotatedString)?.let {
            showGlobalError(it)
        }
    }

    private fun navToTransactionConfirmationNavigation(transactionId: String) {
        nav(
            Arc59ReceiveDetailFragmentDirections.actionArc59ReceiveDetailFragmentToTransactionConfirmationNavigation(
                transactionId
            )
        )
    }

    companion object {
        const val REJECT_CONFIRMATION_ID = 1001L
    }
}
