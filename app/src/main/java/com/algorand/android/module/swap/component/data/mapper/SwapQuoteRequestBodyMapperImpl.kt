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

package com.algorand.android.module.swap.component.data.mapper

import com.algorand.android.module.asset.utils.getSafeAssetIdForRequest
import com.algorand.android.module.swap.component.data.model.SwapQuoteProviderResponse
import com.algorand.android.module.swap.component.data.model.SwapQuoteRequestBody
import com.algorand.android.module.swap.component.data.model.SwapTypeResponse
import com.algorand.android.module.swap.component.domain.model.GetSwapQuoteRequestPayload
import com.algorand.android.module.swap.component.domain.model.SwapQuoteProvider
import com.algorand.android.module.swap.component.domain.model.SwapType
import javax.inject.Inject

internal class SwapQuoteRequestBodyMapperImpl @Inject constructor(
) : SwapQuoteRequestBodyMapper {

    override fun invoke(payload: GetSwapQuoteRequestPayload): SwapQuoteRequestBody {
        return with(payload) {
            SwapQuoteRequestBody(
                providers = getProvidersList(providers),
                swapperAddress = accountAddress,
                swapType = getSwapType(swapType),
                deviceId = deviceId,
                assetInId = getSafeAssetIdForRequest(fromAssetId),
                assetOutId = getSafeAssetIdForRequest(toAssetId),
                amount = amount,
                slippage = slippage
            )
        }
    }

    private fun getProvidersList(provider: List<SwapQuoteProvider>): List<String> {
        return provider.mapNotNull {
            when (it) {
                SwapQuoteProvider.TINYMAN -> SwapQuoteProviderResponse.TINYMAN
                SwapQuoteProvider.TINYMAN_V2 -> SwapQuoteProviderResponse.TINYMAN_V2
                SwapQuoteProvider.VESTIGE_V3 -> SwapQuoteProviderResponse.VESTIGE_V3
                SwapQuoteProvider.UNKNOWN -> null
            }?.value
        }
    }

    private fun getSwapType(swapType: SwapType): SwapTypeResponse {
        return when (swapType) {
            SwapType.FIXED_INPUT -> SwapTypeResponse.FIXED_INPUT
            SwapType.FIXED_OUTPUT -> SwapTypeResponse.FIXED_OUTPUT
            SwapType.UNKNOWN -> SwapTypeResponse.UNKNOWN
        }
    }
}
