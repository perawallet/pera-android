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

package com.algorand.android.ui.asset.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem
import com.algorand.android.ui.asset.detail.model.AssetLineChartData
import com.algorand.android.ui.asset.detail.usecase.GetAssetDetailQuickActionItems
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel.ViewState
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel.ViewState.Idle
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.ui.common.amount.domain.GetSecondaryCurrencyAmountRenderer
import com.algorand.android.ui.compose.widget.chart.extensions.getChangePercentage
import com.algorand.android.utils.MINUS_SIGN
import com.algorand.android.utils.PLUS_SIGN
import com.algorand.android.utils.emptyString
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingFlow
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class AssetHoldingViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountAssetHoldingFlow: GetAccountAssetHoldingFlow,
    private val getAssetDetailQuickActionItems: GetAssetDetailQuickActionItems,
    private val getSecondaryCurrencyAmountRenderer: GetSecondaryCurrencyAmountRenderer,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun init(address: String, asset: Asset) {
        stateDelegate.onState<Idle> {
            getAccountAssetHoldingFlow(address, asset.id).onEach { assetHolding ->
                val viewState = ViewState.Content(
                    balanceAmountRenderer = getBalanceAmountRenderer(asset, assetHolding),
                    balanceSelectedCurrencyRenderer = getBalanceSelectedCurrencyRenderer(asset, assetHolding),
                    quickActionItems = getAssetDetailQuickActionItems(address, asset.id),
                    asset = asset,
                    chartData = null
                )
                stateDelegate.updateState { viewState }
            }.launchIn(viewModelScope)
        }
    }

    fun setChartData(chartData: List<AssetLineChartData>) {
        stateDelegate.onState<ViewState.Content> { content ->
            val balanceChange = chartData.last().value - chartData.first().value
            val data = ViewState.Content.ChartData(
                data = chartData,
                balanceChange = balanceChange,
                balanceChangeRenderer = getBalanceChangeRenderer(content.asset, balanceChange),
                changePercentage = chartData.getChangePercentage()
            )
            stateDelegate.updateState { content.copy(chartData = data) }
        }
    }

    private fun getBalanceChangeRenderer(asset: Asset, balanceChange: Float): AmountRenderer {
        val prefix = getDisplayedCurrencySymbol(asset)
        val sign = PLUS_SIGN.takeIf { balanceChange > 0f } ?: MINUS_SIGN.takeIf { balanceChange < 0f } ?: emptyString()
        val formattedChange = DecimalFormat().apply { maximumFractionDigits = 2 }.format(balanceChange.absoluteValue)
        return AmountRenderer(
            formattedAmount = SimpleFormattedAmount(formattedChange),
            type = Plain,
            prefix = "$sign$prefix"
        )
    }

    private fun getBalanceAmountRenderer(assetDetail: Asset, assetHolding: AssetHolding?): AmountRenderer {
        val assetAmount = (assetHolding?.amount ?: BigInteger.ZERO)
        val decimals = assetDetail.getDecimalsOrZero()
        val amount = PeraAmount(assetAmount, decimals)
        val formattedAmount = PlainFormattedAmount.SimplePlainFormattedAmount(amount, DecimalConfig(decimals))
        val suffix = if (assetDetail.id == ALGO_ID) Currency.ALGO.symbol else assetDetail.shortName.orEmpty()
        return AmountRenderer(formattedAmount, Plain, suffix = suffix)
    }

    private fun getDisplayedCurrencySymbol(asset: Asset): String {
        return if (asset.isAlgo && isPrimaryCurrencyAlgo()) {
            Currency.USD.symbol
        } else {
            getPrimaryCurrencySymbolOrName()
        }
    }

    private fun getBalanceSelectedCurrencyRenderer(asset: Asset, assetHolding: AssetHolding?): AmountRenderer {
        if (assetHolding == null || asset.usdValue == null) {
            val amount = PeraAmount(BigDecimal.ZERO)
            return AmountRenderer(PlainFormattedAmount.SimplePlainFormattedAmount(amount, DecimalConfig(2)), Plain)
        }
        return getSecondaryCurrencyAmountRenderer(asset, assetHolding.amount, Plain)
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val balanceAmountRenderer: AmountRenderer,
            val balanceSelectedCurrencyRenderer: AmountRenderer,
            val quickActionItems: List<AssetDetailQuickActionItem>,
            val asset: Asset,
            val chartData: ChartData?
        ) : ViewState {

            data class ChartData(
                val data: List<AssetLineChartData>,
                val balanceChange: Float,
                val balanceChangeRenderer: AmountRenderer,
                val changePercentage: Float?
            )
        }
    }
}
