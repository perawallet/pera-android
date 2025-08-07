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

import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapWidgetViewModel.SwapQuoteFetchState
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import java.math.BigDecimal
import javax.inject.Inject

private typealias AssetContentViewState = SwapAssetSelectionViewModel.ViewState.Content

internal class DefaultSwapQuoteFetchStateMapper @Inject constructor() : SwapQuoteFetchStateMapper {

    override fun invoke(
        address: String?,
        amount: BigDecimal?,
        slippage: Float?,
        assetInState: SwapAssetSelectionViewModel.ViewState,
        assetOutState: SwapAssetSelectionViewModel.ViewState
    ): SwapQuoteFetchState {
        val isAmountValid = amount != null && amount isGreaterThan BigDecimal.ZERO
        val areAssetsReady = assetInState is AssetContentViewState && assetOutState is AssetContentViewState
        val isAddressValid = !address.isNullOrBlank()
        return if (isAmountValid && areAssetsReady && isAddressValid) {
            val payload = SwapQuotePayload(
                address = address!!,
                assetInId = (assetInState as AssetContentViewState).assetDetail.assetId,
                assetOutId = (assetOutState as AssetContentViewState).assetDetail.assetId,
                amount = amount!!.movePointRight(assetOutState.assetDetail.decimal).toBigInteger(),
                slippage = slippage
            )
            SwapQuoteFetchState.ReadyToFetch(payload)
        } else {
            SwapQuoteFetchState.Idle
        }
    }
}
