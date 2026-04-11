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

@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.algorand.android.ui.swap.widget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem.Auto
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem.Provider
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.mapper.SwapQuoteFetchStateMapper
import com.algorand.android.ui.swap.widget.mapper.SwapWidgetAmountRendererMapper
import com.algorand.android.ui.swap.widget.model.SwapAmountInput
import com.algorand.android.ui.swap.widget.usecase.GetSwapLocalCurrencyAmountFromAssetInput
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Idle
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Quote.QuoteSelection
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Quote.QuoteSelection.Type
import com.algorand.wallet.swap.domain.model.SwapAmountByPercentagePayload
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapQuotes
import com.algorand.wallet.swap.domain.usecase.GetSwapAmountByPercentage
import com.algorand.wallet.swap.domain.usecase.GetSwapQuotes
import com.algorand.wallet.swap.domain.usecase.GetSwapUseLocalCurrencyPreference
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class DefaultSwapWidgetViewModel @Inject constructor(
    private val getSwapQuotes: GetSwapQuotes,
    private val swapQuoteFetchStateMapper: SwapQuoteFetchStateMapper,
    private val amountRendererMapper: SwapWidgetAmountRendererMapper,
    private val getSwapAmountByPercentage: GetSwapAmountByPercentage,
    private val stateDelegate: StateDelegate<ViewState>,
    private val getSwapUseLocalCurrencyPreference: GetSwapUseLocalCurrencyPreference,
    private val getSwapLocalCurrencyAmountFromAssetInput: GetSwapLocalCurrencyAmountFromAssetInput
) : ViewModel(), SwapWidgetViewModel {

    init {
        viewModelScope.launch {
            stateDelegate.setDefaultState(getIdleState(getSwapUseLocalCurrencyPreference()))
        }
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    private val swapAmountInput = SwapAmountInput(Locale.getDefault())

    override fun setAmountInput(amountInput: String) {
        swapAmountInput.setInput(amountInput)
    }

    override fun getAmountInputFlow(): StateFlow<SwapAmountInput.Input> = swapAmountInput.amountInputFlow

    override fun setAmountByPercentage(swapDetails: SwapViewModel.SwapDetails, percentage: Float) {
        viewModelScope.launch {
            val currentState = stateDelegate.state.value
            stateDelegate.updateState { ViewState.Loading }
            val payload = with(swapDetails) {
                SwapAmountByPercentagePayload(address.orEmpty(), assetInId, assetOutId, percentage)
            }
            getSwapAmountByPercentage(payload).use(
                onSuccess = {
                    setAmountInputByPercentage(currentState, swapDetails, it)
                },
                onFailed = { _, _ ->
                    stateDelegate.updateState {
                        val amountRenderers = (currentState as? ViewState.Content)?.amountRenderers
                            ?: amountRendererMapper.getDefaultRenderers(swapDetails.useLocalCurrency)
                        // TODO implement insufficient balance exception based on backend fee decision
                        ViewState.Content(
                            amountRenderers = amountRenderers,
                            contentState = ContentState.Error("Failed to calculate amount by percentage"),
                            useLocalCurrency = swapDetails.useLocalCurrency
                        )
                    }
                }
            )
        }
    }

    private suspend fun setAmountInputByPercentage(
        currentState: ViewState,
        swapDetails: SwapViewModel.SwapDetails,
        amount: BigDecimal
    ) {
        val currentRawInput = swapAmountInput.amountInputFlow.value.rawInput
        if (swapDetails.useLocalCurrency) {
            val amountInput = getSwapLocalCurrencyAmountFromAssetInput(amount, swapDetails.assetInId)
            swapAmountInput.setInput(amountInput)
        } else {
            swapAmountInput.setInput(amount)
        }
        val newRawInput = swapAmountInput.amountInputFlow.value.rawInput
        if (currentRawInput == newRawInput) stateDelegate.updateState { currentState }
    }

    override fun initWidget(
        swapDetailsFlow: Flow<SwapViewModel.SwapDetails>,
        assetInFlow: Flow<SwapAssetSelectionViewModel.ViewState>,
        assetOutFlow: Flow<SwapAssetSelectionViewModel.ViewState>
    ) {
        combine(
            flow = swapDetailsFlow.distinctUntilChanged(),
            flow2 = swapAmountInput.amountInputFlow.debounce(AMOUNT_UPDATE_DEBOUNCE),
            flow3 = assetInFlow,
            flow4 = assetOutFlow,
            transform = swapQuoteFetchStateMapper::invoke
        )
            .flatMapLatest(::updateViewState)
            .onEach { newState -> stateDelegate.updateState { newState } }
            .launchIn(viewModelScope)
    }

    override fun selectQuote(providerItem: SwapQuoteProviderSelectionItem) {
        stateDelegate.onState<ViewState.Content> { content ->
            val quoteState = getContentQuoteState() ?: return
            val quoteSelection = when (providerItem) {
                Auto -> QuoteSelection(quoteId = quoteState.bestOfferQuoteId, selectionType = Type.Auto)
                is Provider -> QuoteSelection(quoteId = providerItem.quoteId, selectionType = Type.Manual)
            }
            val selectedQuote = quoteState.quotes
                .firstOrNull { it.quote.quoteId == quoteSelection.quoteId }
                ?.quote
                ?: return
            val newState = content.copy(
                contentState = quoteState.copy(quoteSelection = quoteSelection),
                amountRenderers = amountRendererMapper.getQuoteRenderers(selectedQuote, content.useLocalCurrency),
            )
            stateDelegate.updateState { newState }
        }
    }

    override fun getContentQuoteState(): ContentState.Quote? {
        return (stateDelegate.state.value as? ViewState.Content)?.contentState as? ContentState.Quote
    }

    private fun updateViewState(quoteFetchState: SwapQuoteFetchState): Flow<ViewState> {
        return if (quoteFetchState.fetchState is SwapQuoteFetchState.State.ReadyToFetch) {
            flow {
                emit(ViewState.Loading)
                val viewState = getSwapQuotes(quoteFetchState.fetchState.payload).use(
                    onSuccess = { getSwapQuoteContentState(quoteFetchState, it) },
                    onFailed = { exception, _ -> getContentErrorState(quoteFetchState, exception) }
                )
                emit(viewState)
            }
        } else {
            flowOf(getIdleState(quoteFetchState.useLocalCurrency))
        }
    }

    private fun getSwapQuoteContentState(quoteFetchState: SwapQuoteFetchState, quotes: SwapQuotes): ViewState.Content {
        val bestQuoteSelection = QuoteSelection(quotes.bestOfferQuoteId, Type.Auto)
        return ViewState.Content(
            amountRendererMapper.getQuoteRenderers(quotes.selectedQuote.quote, quoteFetchState.useLocalCurrency),
            ContentState.Quote(quotes.bestOfferQuoteId, bestQuoteSelection, quotes.quotes),
            quoteFetchState.useLocalCurrency
        )
    }

    private fun getContentErrorState(quoteFetchState: SwapQuoteFetchState, exception: Exception): ViewState.Content {
        return ViewState.Content(
            amountRendererMapper.getDefaultRenderers(quoteFetchState.useLocalCurrency),
            ContentState.Error(exception.message),
            quoteFetchState.useLocalCurrency
        )
    }

    private fun getIdleState(useLocalCurrency: Boolean): ViewState {
        return ViewState.Content(amountRendererMapper.getDefaultRenderers(useLocalCurrency), Idle, useLocalCurrency)
    }

    data class SwapQuoteFetchState(val useLocalCurrency: Boolean, val fetchState: State) {

        sealed interface State {
            data object Idle : State
            data class ReadyToFetch(val payload: SwapQuotePayload) : State
        }
    }

    private companion object {
        const val AMOUNT_UPDATE_DEBOUNCE = 400L
    }
}
