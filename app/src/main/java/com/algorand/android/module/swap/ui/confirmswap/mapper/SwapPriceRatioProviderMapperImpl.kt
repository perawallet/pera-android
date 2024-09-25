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

package com.algorand.android.module.swap.ui.confirmswap.mapper

import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.confirmswap.model.SwapPriceRatioProvider
import javax.inject.Inject

internal class SwapPriceRatioProviderMapperImpl @Inject constructor(
    private val getAssetName: GetAssetName
) : SwapPriceRatioProviderMapper {

    override fun invoke(swapQuote: SwapQuote): SwapPriceRatioProvider {
        return with(swapQuote) {
            SwapPriceRatioProvider(
                fromAssetShortName = getAssetName(fromAssetDetail.shortName),
                fromAmount = fromAssetAmount,
                fromAssetDecimal = fromAssetDetail.fractionDecimals,
                toAssetShortName = getAssetName(toAssetDetail.shortName),
                toAmount = toAssetAmount,
                toAssetDecimal = toAssetDetail.fractionDecimals
            )
        }
    }
}