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

package com.algorand.android.ui.swap.widget.mapper

import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.model.SwapAmountInput
import com.algorand.android.ui.swap.widget.usecase.GetSwapAmountFromLocalCurrencyInput
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapWidgetViewModel.SwapQuoteFetchState
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

private typealias AssetContentViewState = SwapAssetSelectionViewModel.ViewState.Content

internal class DefaultSwapQuoteFetchStateMapper @Inject constructor(
    private val getSwapAmountFromLocalCurrencyInput: GetSwapAmountFromLocalCurrencyInput
) : SwapQuoteFetchStateMapper {

    override fun invoke(
        swapDetails: SwapViewModel.SwapDetails,
        amountInput: SwapAmountInput.Input,
        assetInState: SwapAssetSelectionViewModel.ViewState,
        assetOutState: SwapAssetSelectionViewModel.ViewState
    ): SwapQuoteFetchState {
        val address = swapDetails.address
        val amountAsBigDecimal = amountInput.amountAsBigDecimal
        val isAmountValid = amountAsBigDecimal != null && amountAsBigDecimal isGreaterThan BigDecimal.ZERO
        val areAssetsReady = assetInState is AssetContentViewState && assetOutState is AssetContentViewState
        val isAddressValid = !address.isNullOrBlank()
        return if (isAmountValid && areAssetsReady && isAddressValid) {
            val assetAmount = getAssetInAmount(swapDetails, amountAsBigDecimal, assetInState.assetDetail)
            val payload = SwapQuotePayload(
                address = address,
                assetInId = assetInState.assetDetail.assetId,
                assetOutId = assetOutState.assetDetail.assetId,
                amount = assetAmount,
                slippage = swapDetails.slippage
            )
            SwapQuoteFetchState(swapDetails.useLocalCurrency, SwapQuoteFetchState.State.ReadyToFetch(payload))
        } else {
            SwapQuoteFetchState(swapDetails.useLocalCurrency, SwapQuoteFetchState.State.Idle)
        }
    }

    private fun getAssetInAmount(
        details: SwapViewModel.SwapDetails,
        amount: BigDecimal,
        assetInDetail: SwapSelectedAssetDetail
    ): BigInteger {
        return if (details.useLocalCurrency) {
            getSwapAmountFromLocalCurrencyInput(amount, assetInDetail)
        } else {
            amount.movePointRight(assetInDetail.decimal).toBigInteger()
        }
    }
}
