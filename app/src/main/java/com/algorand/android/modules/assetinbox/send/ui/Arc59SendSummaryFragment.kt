/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.databinding.FragmentArc59SendSummaryBinding
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assetinbox.send.ui.model.Arc59SendSummaryPreview
import com.algorand.android.modules.assetinbox.send.ui.Arc59SendWarningBottomSheet.Companion.ARC59_SEND_CONFIRMATION
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Error
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerScanFailed
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerWaitingForApproval
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Loading
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.NotInitialized
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Success
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.TransactionCancelled
import com.algorand.android.utils.browser.ASSET_INBOX_SUPPORT_URL
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.getCustomClickableSpan
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Arc59SendSummaryFragment : BaseFragment(R.layout.fragment_arc59_send_summary) {

    private val arc59SendSummaryViewModel by viewModels<Arc59SendSummaryViewModel>()

    private val toolbarConfig = ToolbarConfiguration(
        titleResId = R.string.send_to_asset_inbox,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfig)

    private val binding by viewBinding(FragmentArc59SendSummaryBinding::bind)

    private val viewStateCollector: suspend (Arc59SendSummaryPreview) -> Unit = { preview ->
        initPreview(preview)
    }

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    @Inject
    lateinit var arc59SendTransactionSignManager: Arc59SendTransactionSignManager

    private val externalTransactionSignManagerCollector: suspend (ExternalTransactionSignResult) -> Unit = {
        if (it !is Loading) hideLoading()
        when (it) {
            is Success<*> -> sendSignedTransactions(it.signedTransaction)
            is Error -> showTransactionSignResultError(it)
            LedgerScanFailed -> showLedgerNotFoundDialog()
            is LedgerWaitingForApproval -> showLedgerWaitingForApprovalBottomSheet(it)
            Loading -> showLoading()
            NotInitialized -> Unit
            is TransactionCancelled -> showTransactionCancelledError(it)
        }
    }

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener {
        ledgerLoadingDialog = null
        arc59SendTransactionSignManager.stopAllResources()
    }

    private fun showLedgerNotFoundDialog() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun showTransactionSignResultError(error: Error) {
        dismissLedgerDialog()
        context?.run {
            val (title, description) = error.getMessage(this)
            showGlobalError(description, title)
        }
    }

    private fun showTransactionCancelledError(result: TransactionCancelled) {
        dismissLedgerDialog()
        val annotatedString = (result.error as? Error.Defined)?.description
            ?: AnnotatedString(R.string.an_error_occured)
        context?.getXmlStyledString(annotatedString)?.let {
            showGlobalError(it)
        }
    }

    private fun showLedgerWaitingForApprovalBottomSheet(
        ledgerPayload: LedgerWaitingForApproval
    ) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerPayload.ledgerName,
                listener = ledgerLoadingDialogListener,
                currentTransactionIndex = ledgerPayload.currentTransactionIndex,
                totalTransactionCount = ledgerPayload.totalTransactionCount,
                isTransactionIndicatorVisible = ledgerPayload.isTransactionIndicatorVisible
            )
            ledgerLoadingDialog?.showWithStateCheck(
                childFragmentManager,
                ledgerPayload.ledgerName.orEmpty()
            )
        } else {
            ledgerLoadingDialog?.updateTransactionIndicator(ledgerPayload.currentTransactionIndex)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        arc59SendSummaryViewModel.initializePreview()
        arc59SendTransactionSignManager.setup(viewLifecycleOwner.lifecycle)
    }

    override fun onResume() {
        super.onResume()
        initSavedStateListener()
    }

    private fun initUi() {
        with(binding) {
            sendButton.setOnClickListener { navToArc59SendWarningBottomSheet() }
            cancelButton.setOnClickListener { navToHomeNavigation() }
            val linkTextColor = ContextCompat.getColor(requireContext(), R.color.link_primary)
            val mainDescription = AnnotatedString(
                stringResId = R.string.you_cant_send_this_asset,
                customAnnotationList = listOf(
                    "read_more_click" to getCustomClickableSpan(linkTextColor) { navigateToReadMore() }
                )
            )
            mainDescriptionTextView.apply {
                movementMethod = LinkMovementMethod.getInstance()
                text = context?.getXmlStyledString(mainDescription)
            }
        }
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Boolean>(ARC59_SEND_CONFIRMATION) { isConfirmed ->
            if (isConfirmed) {
                arc59SendSummaryViewModel.createTransactionData()
            }
        }
    }

    private fun navigateToReadMore() {
        context?.openUrl(ASSET_INBOX_SUPPORT_URL)
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = arc59SendSummaryViewModel.viewStateFlow,
            collection = viewStateCollector
        )
        collectLatestOnLifecycle(
            flow = arc59SendTransactionSignManager.arc59SendTransactionSignResultFlow,
            collection = externalTransactionSignManagerCollector
        )
    }

    private fun initPreview(preview: Arc59SendSummaryPreview) {
        with(binding) {
            assetNameTextView.text = preview.assetName
            assetAmountTextView.text = preview.formattedAssetAmount
            minBalanceFeeTextView.text = preview.formattedMinBalanceFee
        }
        preview.showError?.consume()?.let { error ->
            context?.let { showGlobalError(error.parseError(it), tag = baseActivityTag) }
        }
        if (preview.isLoading) showLoading() else hideLoading()
        preview.onNavBack?.consume()?.let { navBack() }
        preview.arc59Transactions?.consume()?.let {
            arc59SendTransactionSignManager.signArc59SendTransaction(it)
        }
        preview.onTxnSendSuccessfully?.consume()?.let { transactionId ->
            navToTransactionConfirmationNavigation(transactionId)
        }
    }

    private fun showLoading() {
        binding.progressbar.root.show()
    }

    private fun hideLoading() {
        binding.progressbar.root.hide()
    }

    private fun sendSignedTransactions(signedTransaction: List<Any?>) {
        dismissLedgerDialog()
        arc59SendSummaryViewModel.sendSignedTransaction(signedTransaction)
    }

    private fun navToHomeNavigation() {
        nav(Arc59SendSummaryFragmentDirections.actionArc59SendSummaryFragmentToHomeNavigation())
    }

    private fun dismissLedgerDialog() {
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun navToTransactionConfirmationNavigation(transactionId: String) {
        nav(
            Arc59SendSummaryFragmentDirections.actionArc59SendSummaryFragmentToTransactionConfirmationNavigation(
                transactionId = transactionId,
                titleResId = R.string.operation_completed
            )
        )
    }

    private fun navToArc59SendWarningBottomSheet() {
        nav(
            Arc59SendSummaryFragmentDirections.actionArc59SendSummaryFragmentToArc59SendWarningBottomSheet()
        )
    }
}
