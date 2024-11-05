/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.keyreg.ui.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.databinding.FragmentKeyRegTransactionBinding
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.keyreg.domain.KeyRegTransactionSignManager
import com.algorand.android.modules.keyreg.ui.presentation.model.KeyRegTransactionFragmentPreview
import com.algorand.android.modules.keyreg.ui.presentation.viewmodel.KeyRegTransactionViewModel
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Error
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerScanFailed
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerWaitingForApproval
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Loading
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.NotInitialized
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Success
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.TransactionCancelled
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KeyRegTransactionFragment : BaseFragment(R.layout.fragment_key_reg_transaction) {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val keyRegTransactionViewModel by viewModels<KeyRegTransactionViewModel>()

    private val binding by viewBinding(FragmentKeyRegTransactionBinding::bind)

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    @Inject
    lateinit var keyRegTransactionSignManager: KeyRegTransactionSignManager

    private val previewStateCollector: suspend (KeyRegTransactionFragmentPreview?) -> Unit = {
        it?.let {
            initPreview(it)
        }
    }

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
        keyRegTransactionSignManager.stopAllResources()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        keyRegTransactionSignManager.setup(viewLifecycleOwner.lifecycle)
        keyRegTransactionViewModel.initUi()
    }

    override fun onResume() {
        super.onResume()
        initSavedStateListener()
    }

    private fun initUi() {
        with(binding) {
            button.setOnClickListener {
                // TODO navToConfirmationBottomSheet()
                keyRegTransactionViewModel.confirmTransaction()
            }
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = keyRegTransactionViewModel.previewState,
            collection = previewStateCollector
        )
        collectLatestOnLifecycle(
            flow = keyRegTransactionSignManager.keyRegTransactionSignResultFlow,
            collection = externalTransactionSignManagerCollector
        )
    }

    private fun initSavedStateListener() {
//        useFragmentResultListenerValue<Boolean>(KEY_REG_CONFIRMATION_RESULT_KEY) { isConfirmed ->
//            if (isConfirmed) {
//                keyRegTransactionViewModel.confirmTransaction()
//            }
//        }
    }

    private fun initPreview(preview: KeyRegTransactionFragmentPreview) {
        // TODO
        binding.text2.text = preview.address
        preview.signTransactionEvent?.consume()?.let { keyRegTxn ->
            keyRegTransactionSignManager.signKeyRegTransaction(keyRegTxn)
        }
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

    private fun showLoading() {
        binding.progressbar.root.show()
    }

    private fun hideLoading() {
        binding.progressbar.root.hide()
    }

    private fun sendSignedTransactions(signedTransaction: List<Any?>) {
        dismissLedgerDialog()
        keyRegTransactionViewModel.sendSignedTransaction(signedTransaction)
    }

    private fun navToConfirmationBottomSheet() {
        // TODO navigate to confirmation bottom sheet
    }

    private fun dismissLedgerDialog() {
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }
}
