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

package com.algorand.android.modules.swap.confirmswap.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.module.swap.component.domain.usecase.CreateSwapQuoteTransactions
import com.algorand.android.module.swap.ui.GetSwapError
import com.algorand.android.module.swap.ui.confirmswap.SignSwapTransactionManager
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPreview
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult
import com.algorand.android.module.swap.ui.confirmswap.usecase.GetConfirmSwapPreview
import com.algorand.android.module.swap.ui.confirmswap.usecase.GetSwapTransactionStatusNavArgs
import com.algorand.android.module.swap.ui.confirmswap.usecase.UpdateSlippageTolerance
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusNavArgs
import com.algorand.android.modules.tracking.swap.confirmswap.ConfirmSwapConfirmClickEventTracker
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ConfirmSwapViewModel @Inject constructor(
    private val getConfirmSwapPreview: GetConfirmSwapPreview,
    private val updateSlippageTolerance: UpdateSlippageTolerance,
    private val confirmClickEventTracker: ConfirmSwapConfirmClickEventTracker,
    private val getSwapTransactionStatusNavArgs: GetSwapTransactionStatusNavArgs,
    private val signSwapTransactionManager: SignSwapTransactionManager,
    private val createSwapQuoteTransactions: CreateSwapQuoteTransactions,
    private val getSwapError: GetSwapError,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private var _swapQuote = savedStateHandle.getOrThrow<SwapQuote>(SWAP_QUOTE_KEY)
    val swapQuote: SwapQuote
        get() = _swapQuote

    private val _confirmSwapPreviewFlow = MutableStateFlow<ConfirmSwapPreview?>(null)
    val confirmSwapPreviewFlow: StateFlow<ConfirmSwapPreview?>
        get() = _confirmSwapPreviewFlow

    val signSwapTransactionResultFlow: Flow<Event<SignSwapTransactionResult>?>
        get() = signSwapTransactionManager.signSwapTransactionResultFlow

    private var signSwapTransactionsJob: Job? = null

    init {
        initPreviewFlow()
    }

    private fun initPreviewFlow() {
        viewModelScope.launchIO {
            _confirmSwapPreviewFlow.value = getConfirmSwapPreview(swapQuote)
        }
    }

    fun getSwitchedPriceRatio(): AnnotatedString? {
        return _confirmSwapPreviewFlow.value?.getSwitchedPriceRatio()
    }

    fun setupSwapTransactionSignManager(lifecycle: Lifecycle) {
        signSwapTransactionManager.setup(lifecycle)
    }

    fun getSwapTxnStatusNavArgs(signedTransaction: List<SwapQuoteTransaction>): SwapTransactionStatusNavArgs {
        return getSwapTransactionStatusNavArgs(swapQuote, signedTransaction)
    }

    fun getSlippageTolerance() = _swapQuote.slippage

    fun onSlippageToleranceUpdated(slippageTolerance: Float) {
        viewModelScope.launch {
            _confirmSwapPreviewFlow.update {
                if (it == null) return@update null
                updateSlippageTolerance(
                    slippageTolerance = slippageTolerance,
                    swapQuote = swapQuote,
                    preview = it
                )
            }
        }
    }

    fun onConfirmSwapClick() {
        val priceImpactWarningStatus = _confirmSwapPreviewFlow.value?.priceImpactWarningStatus ?: return
        if (priceImpactWarningStatus.isConfirmationRequired) {
            _confirmSwapPreviewFlow.update {
                val basePriceImpactValue = priceImpactWarningStatus.percentageRange.first.toLong()
                it?.copy(navToSwapConfirmationBottomSheetEvent = Event(basePriceImpactValue))
            }
        } else {
            createQuoteAndUpdateUi()
        }
    }

    fun onSwapPriceImpactConfirmationResult(isConfirmed: Boolean) {
        if (isConfirmed) createQuoteAndUpdateUi()
    }

    private fun createQuoteAndUpdateUi() {
        _confirmSwapPreviewFlow.update {
            it?.copy(isLoading = true)
        }
        viewModelScope.launch {
            confirmClickEventTracker.logConfirmSwapClickEvent()
            createSwapQuoteTransactions(swapQuote.quoteId, swapQuote.accountAddress).use(
                onSuccess = {
                    signTransactions(it)
                },
                onFailed = { exception, _ ->
                    _confirmSwapPreviewFlow.update {
                        it?.copy(
                            errorEvent = Event(getSwapError(exception.message.orEmpty())),
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    private fun signTransactions(transactions: List<SwapQuoteTransaction>) {
        if (signSwapTransactionsJob?.isActive == true) {
            signSwapTransactionsJob?.cancel()
        }
        signSwapTransactionsJob = viewModelScope.launch {
            signSwapTransactionManager.sign(transactions)
        }
    }

    fun onLedgerDialogCancelled() {
        signSwapTransactionManager.stopAllResources()
    }

    fun clearCachedTransactions() {
        signSwapTransactionManager.clearCachedTransactions()
    }

    fun processWaitingTransaction() {
        signSwapTransactionManager.signCachedTransaction()
    }

    companion object {
        private const val SWAP_QUOTE_KEY = "swapQuote"
    }
}
