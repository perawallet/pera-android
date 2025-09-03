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
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Idle
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Quote.QuoteSelection
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Quote.QuoteSelection.Type
import com.algorand.wallet.swap.domain.model.SwapAmountByPercentagePayload
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.usecase.GetSwapAmountByPercentage
import com.algorand.wallet.swap.domain.usecase.GetSwapQuotes
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), SwapWidgetViewModel {

    init {
        stateDelegate.setDefaultState(getIdleState())
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    private val amountInputFlow = MutableStateFlow<String>("")

    override fun setAmountInput(amountInput: String) {
        amountInputFlow.value = amountInput
    }

    override fun getAmountInputFlow(): StateFlow<String> = amountInputFlow.asStateFlow()

    override fun setAmountByPercentage(swapDetails: SwapViewModel.SwapDetails, percentage: Int) {
        viewModelScope.launch {
            val currentState = stateDelegate.state.value
            stateDelegate.updateState { ViewState.Loading }
            val payload = with(swapDetails) {
                SwapAmountByPercentagePayload(address.orEmpty(), assetInId, assetOutId, percentage.toFloat())
            }
            getSwapAmountByPercentage(payload).use(
                onSuccess = {
                    amountInputFlow.value = it.toPlainString()
                },
                onFailed = { _, _ ->
                    stateDelegate.updateState {
                        val amountRenderers = (currentState as? ViewState.Content)?.amountRenderers
                            ?: amountRendererMapper.getDefaultRenderers()
                        // TODO implement insufficient balance exception based on backend fee decision
                        ViewState.Content(
                            amountRenderers = amountRenderers,
                            contentState = ContentState.Error("Failed to calculate amount by percentage")
                        )
                    }
                }
            )
        }
    }

    override fun initWidget(
        swapDetailsFlow: Flow<SwapViewModel.SwapDetails>,
        assetInFlow: Flow<SwapAssetSelectionViewModel.ViewState>,
        assetOutFlow: Flow<SwapAssetSelectionViewModel.ViewState>
    ) {
        combine(
            flow = swapDetailsFlow.distinctUntilChanged(),
            flow2 = amountInputFlow.debounce(AMOUNT_UPDATE_DEBOUNCE),
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
            stateDelegate.updateState {
                val quoteSelection = when (providerItem) {
                    Auto -> QuoteSelection(quoteId = quoteState.bestOfferQuoteId, selectionType = Type.Auto)
                    is Provider -> QuoteSelection(quoteId = providerItem.quoteId, selectionType = Type.Manual)
                }
                val selectedQuote = quoteState.quotes.first { it.quote.quoteId == quoteSelection.quoteId }.quote
                content.copy(
                    contentState = quoteState.copy(quoteSelection = quoteSelection),
                    amountRenderers = amountRendererMapper.getQuoteRenderers(selectedQuote),
                )
            }
        }
    }

    override fun getContentQuoteState(): ContentState.Quote? {
        return (stateDelegate.state.value as? ViewState.Content)?.contentState as? ContentState.Quote
    }

    private fun updateViewState(quoteFetchState: SwapQuoteFetchState): Flow<ViewState> {
        return if (quoteFetchState is SwapQuoteFetchState.ReadyToFetch) {
            flow {
                emit(ViewState.Loading)
                getSwapQuotes(quoteFetchState.payload).use(
                    onSuccess = {
                        val bestQuoteSelection = QuoteSelection(it.bestOfferQuoteId, Type.Auto)
                        val viewState = ViewState.Content(
                            amountRendererMapper.getQuoteRenderers(it.selectedQuote.quote),
                            ContentState.Quote(it.bestOfferQuoteId, bestQuoteSelection, it.quotes)
                        )
                        emit(viewState)
                    },
                    onFailed = { exception, _ ->
                        val viewState = ViewState.Content(
                            amountRendererMapper.getDefaultRenderers(),
                            ContentState.Error(exception.message)
                        )
                        emit(viewState)
                    }
                )
            }
        } else {
            flowOf(getIdleState())
        }
    }

    private fun getIdleState(): ViewState = ViewState.Content(amountRendererMapper.getDefaultRenderers(), Idle)

    sealed interface SwapQuoteFetchState {
        data object Idle : SwapQuoteFetchState
        data class ReadyToFetch(val payload: SwapQuotePayload) : SwapQuoteFetchState
    }

    private companion object {
        const val AMOUNT_UPDATE_DEBOUNCE = 400L
    }
}
