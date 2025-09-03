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

package com.algorand.android.ui.swap.confirmation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.swap.confirmswapconfirmation.SwapConfirmationBottomSheet.Companion.CONFIRMATION_SUCCESS_KEY
import com.algorand.android.modules.swap.ledger.signwithledger.ui.model.LedgerDialogPayload
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayError
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayLedgerNotFoundDialog
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.NavigateToLedgerWaitingForApprovalDialog
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.NavigateToTransactionStatus
import com.algorand.android.utils.browser.openTinymanFaqPriceImpactUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.useFragmentResultListenerValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwapConfirmationFragment : BaseFragment(0), SwapConfirmationScreenListener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val swapConfirmationViewModel: SwapConfirmationViewModel by viewModels()

    private val args: SwapConfirmationFragmentArgs by navArgs()

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    private val viewEventCollector: suspend (SwapConfirmationViewModel.ViewEvent) -> Unit = { viewEvent ->
        when (viewEvent) {
            is DisplayError -> displayError(viewEvent.errorType)
            is NavigateToLedgerWaitingForApprovalDialog -> showLedgerWaitingForApprovalBottomSheet(viewEvent.payload)
            DisplayLedgerNotFoundDialog -> nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
            is NavigateToTransactionStatus -> navigateToTransactionStatus(viewEvent)
        }
    }

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener {
        ledgerLoadingDialog = null
        swapConfirmationViewModel.stopResources()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createComposeView {
            SwapConfirmationScreen(swapConfirmationViewModel, listener = this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swapConfirmationViewModel.init(args.swapQuote)
        collectLatestOnLifecycle(swapConfirmationViewModel.viewEvent, viewEventCollector)
        swapConfirmationViewModel.initializeSwapTransactionSignManager(viewLifecycleOwner.lifecycle)
    }

    override fun onResume() {
        super.onResume()
        useFragmentResultListenerValue<Boolean>(CONFIRMATION_SUCCESS_KEY) { isConfirmed ->
            if (isConfirmed) swapConfirmationViewModel.confirmSwap()
        }
    }

    override fun onNavBackClick() {
        navBack()
    }

    override fun onSlippageToleranceInfoClick() {
        nav(SwapConfirmationFragmentDirections.actionSwapConfirmationFragmentToSwapSlippageToleranceInfoBottomSheet())
    }

    override fun onPriceImpactInfoClick() {
        nav(SwapConfirmationFragmentDirections.actionSwapConfirmationFragmentToSwapPriceImpactInfoBottomSheet())
    }

    override fun onExchangeFeeInfoClick() {
        nav(SwapConfirmationFragmentDirections.actionSwapConfirmationFragmentToSwapExchangeFeeInfoBottomSheet())
    }

    override fun onTinymanFaqPriceImpactUrlClick() {
        context?.openTinymanFaqPriceImpactUrl()
    }

    private fun showLedgerWaitingForApprovalBottomSheet(ledgerDialogPayload: LedgerDialogPayload) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerDialogPayload.ledgerName,
                listener = ledgerLoadingDialogListener,
                currentTransactionIndex = ledgerDialogPayload.currentTransactionIndex,
                totalTransactionCount = ledgerDialogPayload.totalTransactionCount,
                isTransactionIndicatorVisible = ledgerDialogPayload.isTransactionIndicatorVisible
            )
            ledgerLoadingDialog?.showWithStateCheck(childFragmentManager, ledgerDialogPayload.ledgerName.orEmpty())
        } else {
            ledgerLoadingDialog?.updateTransactionIndicator(ledgerDialogPayload.currentTransactionIndex)
        }
    }

    private fun displayError(errorType: DisplayError.ErrorType) {
        val message = when (errorType) {
            DisplayError.ErrorType.Generic -> getString(R.string.an_error_occured)
            is DisplayError.ErrorType.Api -> errorType.message
            is DisplayError.ErrorType.Local -> context?.getXmlStyledString(errorType.description)?.toString().orEmpty()
        }
        showGlobalError(message)
    }

    private fun navigateToTransactionStatus(navData: NavigateToTransactionStatus) {
//        nav(
//            SwapConfirmationFragmentDirections.actionSwapConfirmationFragmentToSwapTransactionStatusFragment(
//                navData.legacySwapQuote,
//                navData.swapQuoteTransactions.toTypedArray()
//            )
//        )
    }
}
