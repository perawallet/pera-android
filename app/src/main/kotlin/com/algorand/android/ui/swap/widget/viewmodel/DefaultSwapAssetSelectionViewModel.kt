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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.DecimalConfig.MinDecimalType.Fixed
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel.ViewState
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import com.algorand.wallet.swap.domain.usecase.GetSelectedSwapAssetDetail
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DefaultSwapAssetSelectionViewModel @Inject constructor(
    private val getSelectedSwapAssetDetail: GetSelectedSwapAssetDetail,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), SwapAssetSelectionViewModel {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    override fun initAssetDetail(addressFlow: Flow<String?>, assetIdFlow: Flow<Long>) {
        combine(addressFlow, assetIdFlow) { address, assetId ->
            if (address.isNullOrBlank()) {
                stateDelegate.updateState { ViewState.Loading }
            } else {
                getSelectedSwapAssetDetail(address, assetId).use(
                    onSuccess = { assetDetail ->
                        stateDelegate.updateState { ViewState.Content(assetDetail, getBalanceRenderer(assetDetail)) }
                    },
                    onFailed = { _, _ ->
                        stateDelegate.updateState { ViewState.Error }
                    }
                )
            }
        }
            .onStart { stateDelegate.updateState { ViewState.Loading } }
            .launchIn(viewModelScope)
    }

    private fun getBalanceRenderer(assetDetail: SwapSelectedAssetDetail): AmountRenderer {
        val balance = PeraAmount(BigDecimal(assetDetail.amount).movePointLeft(assetDetail.decimal))
        val decimalConfig = DecimalConfig(maxDecimals = assetDetail.decimal, minDecimals = Fixed(2))
        val formatter = SimplePlainFormattedAmount(balance, decimalConfig)
        return AmountRenderer(
            formattedAmount = formatter,
            type = AmountRenderer.RenderType.Plain,
            prefix = Currency.ALGO.symbol.takeIf { assetDetail.assetId == ALGO_ID },
            suffix = assetDetail.unitName.takeIf { assetDetail.assetId != ALGO_ID }
        )
    }
}
