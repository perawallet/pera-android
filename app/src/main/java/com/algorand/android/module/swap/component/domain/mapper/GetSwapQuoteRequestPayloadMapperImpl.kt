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

package com.algorand.android.module.swap.component.domain.mapper

import com.algorand.android.module.swap.component.domain.model.GetSwapQuotePayload
import com.algorand.android.module.swap.component.domain.model.GetSwapQuoteRequestPayload
import com.algorand.android.module.swap.component.domain.model.SwapQuoteProvider.TINYMAN
import com.algorand.android.module.swap.component.domain.model.SwapQuoteProvider.TINYMAN_V2
import com.algorand.android.module.swap.component.domain.model.SwapQuoteProvider.VESTIGE_V3
import com.algorand.android.module.swap.component.domain.model.SwapType
import javax.inject.Inject

internal class GetSwapQuoteRequestPayloadMapperImpl @Inject constructor() : GetSwapQuoteRequestPayloadMapper {

    override suspend fun invoke(payload: GetSwapQuotePayload, deviceId: String): GetSwapQuoteRequestPayload {
        return GetSwapQuoteRequestPayload(
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId,
            amount = payload.amount,
            swapType = SwapType.getDefaultSwapType(),
            accountAddress = payload.accountAddress,
            deviceId = deviceId,
            slippage = payload.slippage,
            providers = listOf(TINYMAN, TINYMAN_V2, VESTIGE_V3)
        )
    }
}
