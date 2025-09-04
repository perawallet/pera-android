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

package com.algorand.android.ui.swap.topfive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryFiatAmountRenderer
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.android.ui.swap.topfive.model.TopSwapPairItem
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import com.algorand.wallet.swap.domain.usecase.GetTopSwapPairs
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltViewModel
class DefaultTopSwapPairsViewModel @Inject constructor(
    private val getTopSwapPairs: GetTopSwapPairs,
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val getCompactPrimaryFiatAmountRenderer: GetCompactPrimaryFiatAmountRenderer,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), TopSwapPairsViewModel {

    private val viewStateFlow = MutableStateFlow<ViewState>(ViewState.Idle)

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    override fun init(swapWidgetViewState: Flow<SwapWidgetViewModel.ViewState>) {
        stateDelegate.onState<ViewState.Idle> {
            initTopFiveSwaps()
            combine(swapWidgetViewState, viewStateFlow) { widgetState, viewState ->
                val widgetContentState = (widgetState as? SwapWidgetViewModel.ViewState.Content)?.contentState
                if (widgetContentState is SwapWidgetViewModel.ViewState.Content.ContentState.Idle) {
                    stateDelegate.updateState { viewState }
                } else {
                    stateDelegate.updateState { ViewState.Idle }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun initTopFiveSwaps() {
        viewModelScope.launch {
            viewStateFlow.value = ViewState.Loading
            getTopSwapPairs().use(
                onSuccess = { result ->
                    val items = result.details.map { mapToTopSwapPairItem(it) }
                    viewStateFlow.value = if (items.isEmpty()) ViewState.Error else ViewState.Content(items)
                },
                onFailed = { _, _ ->
                    viewStateFlow.value = ViewState.Error
                }
            )
        }
    }

    private fun mapToTopSwapPairItem(detail: TopSwapPairs.Detail): TopSwapPairItem {
        return with(detail) {
            TopSwapPairItem(
                assetInId = assetA.id,
                assetInShortName = assetA.shortName,
                assetInIconDrawable = assetIconDrawableMapper.map(assetA),
                assetOutId = assetB.id,
                assetOutShortName = assetB.shortName,
                assetOutIconDrawable = assetIconDrawableMapper.map(assetB),
                volume = getCompactPrimaryFiatAmountRenderer(PeraAmount(detail.volumeUsd), Plain)
            )
        }
    }
}
