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

package com.algorand.android.ui.swap.confirmation.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.swap.confirmswap.domain.SwapTransactionSignManager
import com.algorand.android.modules.swap.confirmswap.domain.model.SwapQuoteTransaction
import com.algorand.android.modules.swap.confirmswap.domain.usecase.CreateSwapQuoteTransactionsUseCase
import com.algorand.android.modules.swap.ledger.signwithledger.ui.model.LedgerDialogPayload
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerScanFailed
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerWaitingForApproval
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Loading
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Success
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.TransactionCancelled
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.android.ui.swap.confirmation.mapper.LegacySwapQuoteMapper
import com.algorand.android.ui.swap.confirmation.mapper.SwapConfirmationContentMapper
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.Level1
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.Level2
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.Level3
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.NoWarning
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayError
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayError.ErrorType
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayError.ErrorType.Api
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayError.ErrorType.Generic
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.DisplayError.ErrorType.Local
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.NavigateToPriceImpactConfirmation
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewEvent.NavigateToTransactionStatus
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Idle
import com.algorand.android.utils.DataResource
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SwapConfirmationViewModel @Inject constructor(
    private val contentMapper: SwapConfirmationContentMapper,
    private val createSwapQuoteTransactions: CreateSwapQuoteTransactionsUseCase,
    private val swapTransactionSignManager: SwapTransactionSignManager,
    private val legacySwapQuoteMapper: LegacySwapQuoteMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private var confirmTransactionJob: Job? = null

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun init(quote: SwapQuoteV2) {
        stateDelegate.onState<Idle> {
            viewModelScope.launch {
                val contentState = contentMapper.map(quote)
                stateDelegate.updateState { contentState }
            }
        }
    }

    fun confirmSwapWithCheckingPriceImpact() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val status = contentState.priceImpact.warningStatus
            when (status) {
                NoWarning -> confirmSwap()
                is Level1 -> confirmSwap()
                is Level2 -> {
                    val event = NavigateToPriceImpactConfirmation(status.threshold.toLong())
                    eventDelegate.sendEvent(viewModelScope, event)
                }
                is Level3 -> Unit
            }
        }
    }

    fun confirmSwap() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            if (confirmTransactionJob?.isActive == true) {
                confirmTransactionJob?.cancel()
            }
            stateDelegate.updateState { contentState.copy(contentState = ContentState.Loading) }
            swapTransactionSignManager.manualStopAllResources()
            confirmTransactionJob = viewModelScope.launch {
                val quoteId = contentState.quote.quoteId
                val accountAddress = contentState.quote.accountAddress
                createSwapQuoteTransactions.createQuoteTransactions(quoteId, accountAddress)
                    .useSuspended(onSuccess = ::signTransactions, onFailed = ::displayFailedToCreateTxnError)
            }
        }
    }

    fun initializeSwapTransactionSignManager(lifecycle: Lifecycle) {
        swapTransactionSignManager.setup(lifecycle)
    }

    fun stopResources() {
        swapTransactionSignManager.stopAllResources()
    }

    private suspend fun signTransactions(transactions: List<SwapQuoteTransaction>) {
        swapTransactionSignManager.signSwapQuoteTransaction(transactions)
        swapTransactionSignManager.swapTransactionSignResultFlow.collectLatest { result ->
            when (result) {
                is Success<*> -> navigateToTransactionStatusFragment(result)
                LedgerScanFailed -> displayLedgerNotFoundDialog()
                is LedgerWaitingForApproval -> displayLedgerWaitingForApprovalDialog(result)
                Loading -> stateDelegate.onState<ViewState.Content> { contentState ->
                    if (contentState.contentState != ContentState.Loading) {
                        stateDelegate.updateState { contentState.copy(contentState = ContentState.Loading) }
                    }
                }
                is ExternalTransactionSignResult.Error.Api -> displayError(Api(result.errorMessage))
                is ExternalTransactionSignResult.Error.Defined -> displayError(Local(result.description))
                is TransactionCancelled -> {
                    val error = (result.error as? ExternalTransactionSignResult.Error.Defined)?.description
                    val errorType = if (error != null) Local(error) else Generic
                    displayError(errorType)
                }
                ExternalTransactionSignResult.NotInitialized -> Unit
            }
        }
    }

    private suspend fun displayError(errorType: ErrorType) {
        hideLoading()
        eventDelegate.sendEvent(DisplayError(errorType))
    }

    private suspend fun navigateToTransactionStatusFragment(result: Success<*>) {
        stateDelegate.onState<ViewState.Content> { content ->
            hideLoading()
            val signedTxns = result.signedTransaction as? List<SwapQuoteTransaction>
            val event = if (signedTxns != null) {
                val legacySwapQuote = legacySwapQuoteMapper(content.quote)
                NavigateToTransactionStatus(legacySwapQuote, signedTxns)
            } else {
                DisplayError(Generic)
            }
            stateDelegate.updateState { content.copy(contentState = ContentState.Idle) }
            eventDelegate.sendEvent(event)
        }
    }

    private suspend fun displayFailedToCreateTxnError(error: DataResource.Error<List<SwapQuoteTransaction>>) {
        hideLoading()
        val errorType = if (error.exception is IOException) {
            Local(AnnotatedString(R.string.the_internet_connection))
        } else {
            Generic
        }
        eventDelegate.sendEvent(DisplayError(errorType))
    }

    private suspend fun displayLedgerNotFoundDialog() {
        hideLoading()
        eventDelegate.sendEvent(ViewEvent.DisplayLedgerNotFoundDialog)
    }

    private suspend fun displayLedgerWaitingForApprovalDialog(result: LedgerWaitingForApproval) {
        hideLoading()
        val ledgerPayload = LedgerDialogPayload(
            result.ledgerName,
            result.currentTransactionIndex,
            result.totalTransactionCount,
            result.isTransactionIndicatorVisible
        )
        eventDelegate.sendEvent(ViewEvent.NavigateToLedgerWaitingForApprovalDialog(ledgerPayload))
    }

    private fun hideLoading() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            stateDelegate.updateState { contentState.copy(contentState = ContentState.Idle) }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val quote: SwapQuoteV2,
            val accountDisplayName: AccountDisplayName,
            val accountIconDrawable: AccountIconDrawablePreview,
            val priceImpact: SwapPriceImpact,
            val assetInDetail: AssetDetail,
            val assetOutDetail: AssetDetail,
            val exchangeFee: AmountRenderer,
            val peraFee: AmountRenderer,
            val minReceivedAssetAmount: AmountRenderer,
            val assetInToOutPriceRatio: PriceRatio,
            val assetOutToInPriceRatio: PriceRatio,
            val contentState: ContentState = ContentState.Idle
        ) : ViewState {

            sealed interface ContentState {
                data object Idle : ContentState
                data object Loading : ContentState
            }

            class AssetDetail(
                val amount: AmountRenderer,
                val approximateValue: AmountRenderer,
                val shortName: String?,
                val assetIconDrawable: AssetIconDrawable,
                val verificationTier: VerificationTierConfiguration
            )

            data class PriceRatio(
                val ratio: AmountRenderer,
                val firstAssetUnitName: String,
                val secondAssetUnitName: String
            )
        }
    }

    sealed interface ViewEvent {
        data class NavigateToTransactionStatus(
            val legacySwapQuote: com.algorand.android.modules.swap.assetswap.domain.model.SwapQuote,
            val swapQuoteTransactions: List<SwapQuoteTransaction>
        ) : ViewEvent

        data object DisplayLedgerNotFoundDialog : ViewEvent
        data class NavigateToLedgerWaitingForApprovalDialog(val payload: LedgerDialogPayload) : ViewEvent
        data class NavigateToPriceImpactConfirmation(val priceImpactPercentage: Long) : ViewEvent
        data class DisplayError(val errorType: ErrorType) : ViewEvent {
            sealed interface ErrorType {
                data object Generic : ErrorType
                data class Api(val message: String) : ErrorType
                data class Local(val description: AnnotatedString, val title: AnnotatedString? = null) : ErrorType
            }
        }
    }
}
