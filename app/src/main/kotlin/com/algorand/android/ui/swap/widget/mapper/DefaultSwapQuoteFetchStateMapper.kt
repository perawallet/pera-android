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
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapWidgetViewModel.SwapQuoteFetchState
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import java.math.BigDecimal
import javax.inject.Inject

private typealias AssetContentViewState = SwapAssetSelectionViewModel.ViewState.Content

internal class DefaultSwapQuoteFetchStateMapper @Inject constructor() : SwapQuoteFetchStateMapper {

    override fun invoke(
        swapDetails: SwapViewModel.SwapDetails,
        amount: String,
        assetInState: SwapAssetSelectionViewModel.ViewState,
        assetOutState: SwapAssetSelectionViewModel.ViewState
    ): SwapQuoteFetchState {
        val address = swapDetails.address
        val amountAsBigDecimal = amount.toBigDecimalOrNull()
        val isAmountValid = amountAsBigDecimal != null && amountAsBigDecimal isGreaterThan BigDecimal.ZERO
        val areAssetsReady = assetInState is AssetContentViewState && assetOutState is AssetContentViewState
        val isAddressValid = !address.isNullOrBlank()
        return if (isAmountValid && areAssetsReady && isAddressValid) {
            val payload = SwapQuotePayload(
                address = address,
                assetInId = assetInState.assetDetail.assetId,
                assetOutId = assetOutState.assetDetail.assetId,
                amount = amountAsBigDecimal.movePointRight(assetInState.assetDetail.decimal).toBigInteger(),
                slippage = swapDetails.slippage
            )
            SwapQuoteFetchState.ReadyToFetch(payload)
        } else {
            SwapQuoteFetchState.Idle
        }
    }
}
