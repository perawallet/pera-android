/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.ui.assetswap.usecase

import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import javax.inject.Inject

internal class GetFromSelectedAssetAmountDetailUseCase @Inject constructor() : GetFromSelectedAssetAmountDetail {

    override suspend fun invoke(
        swapQuote: SwapQuote,
        previousAmount: String?
    ): AssetSwapPreview.SelectedAssetAmountDetail {
        val amount = if (previousAmount.isNullOrBlank()) {
            swapQuote.fromAssetAmount
                .movePointLeft(swapQuote.fromAssetDetail.fractionDecimals)
                .stripTrailingZeros()
                .toPlainString()
        } else {
            previousAmount
        }
        return AssetSwapPreview.SelectedAssetAmountDetail(
            amount = amount,
            formattedApproximateValue = swapQuote.fromAssetAmountInSelectedCurrentFormattedValue,
            assetDecimal = swapQuote.fromAssetDetail.fractionDecimals
        )
    }
}
