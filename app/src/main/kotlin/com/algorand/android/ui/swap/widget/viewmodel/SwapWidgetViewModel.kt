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

package com.algorand.android.ui.swap.widget.viewmodel

import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.swap.configuration.view.SwapConfigurationResult
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.viewmodel.StateViewModel
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

interface SwapWidgetViewModel : StateViewModel<ViewState> {

    fun setAmountInput(amount: BigDecimal?)

    fun applyConfigurations(configuration: SwapConfigurationResult)

    fun setMaxAmountInput()

    fun initWidget(
        addressFlow: Flow<String?>,
        assetInFlow: Flow<SwapAssetSelectionViewModel.ViewState>,
        assetOutFlow: Flow<SwapAssetSelectionViewModel.ViewState>
    )

    sealed interface ViewState {

        data object Loading : ViewState
        data class Content(
            val amountRenderers: AmountRenderers,
            val contentState: ContentState
        ) : ViewState {
            sealed interface ContentState {
                data object Idle : ContentState

                data class Error(val message: String?) : ContentState

                data class Quote(
                    val selectedQuoteId: Long,
                    val bestOfferQuoteId: Long,
                    val quotes: List<SwapQuoteDetail>
                ) : ContentState {

                    val selectedQuoteDetail
                        get() = quotes.first { it.quote.quoteId == selectedQuoteId }
                }
            }

            data class AmountRenderers(
                val assetInPrimaryAmountHint: AmountRenderer,
                val assetInSecondaryAmount: AmountRenderer,
                val assetOutPrimaryAmount: AmountRenderer,
                val assetOutSecondaryAmount: AmountRenderer,
            )
        }
    }
}
