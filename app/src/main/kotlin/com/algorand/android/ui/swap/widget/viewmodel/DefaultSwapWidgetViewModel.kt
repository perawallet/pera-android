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
import com.algorand.android.ui.swap.widget.mapper.SwapQuoteFetchStateMapper
import com.algorand.android.ui.swap.widget.mapper.SwapWidgetAmountRendererMapper
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Idle
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.usecase.GetSwapQuotes
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class DefaultSwapWidgetViewModel @Inject constructor(
    private val getSwapQuotes: GetSwapQuotes,
    private val swapQuoteFetchStateMapper: SwapQuoteFetchStateMapper,
    private val amountRendererMapper: SwapWidgetAmountRendererMapper,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), SwapWidgetViewModel {

    init {
        stateDelegate.setDefaultState(getIdleState())
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    private val amountInputFlow = MutableStateFlow<BigDecimal?>(null)
    private val slippageFlow = MutableStateFlow<Float?>(null)

    override fun setAmountInput(amount: BigDecimal?) {
        amountInputFlow.value = amount
    }

    override fun initWidget(
        addressFlow: Flow<String?>,
        assetInFlow: Flow<SwapAssetSelectionViewModel.ViewState>,
        assetOutFlow: Flow<SwapAssetSelectionViewModel.ViewState>
    ) {
        combine(
            flow = addressFlow,
            flow2 = amountInputFlow.debounce(AMOUNT_UPDATE_DEBOUNCE),
            flow3 = slippageFlow,
            flow4 = assetInFlow,
            flow5 = assetOutFlow,
            transform = swapQuoteFetchStateMapper::invoke
        )
            .flatMapLatest(::updateViewState)
            .onEach { newState -> stateDelegate.updateState { newState } }
            .launchIn(viewModelScope)
    }

    private fun updateViewState(quoteFetchState: SwapQuoteFetchState): Flow<ViewState> {
        return if (quoteFetchState is SwapQuoteFetchState.ReadyToFetch) {
            flow {
                emit(ViewState.Loading)
                getSwapQuotes(quoteFetchState.payload).use(
                    onSuccess = {
                        val viewState = ViewState.Content(
                            amountRendererMapper.getQuoteRenderers(it.selectedQuote.quote),
                            ContentState.Quote(it.selectedQuoteId, it.bestOfferQuoteId, it.quotes)
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
