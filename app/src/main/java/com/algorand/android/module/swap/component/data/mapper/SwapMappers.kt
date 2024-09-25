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

import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.module.swap.component.data.model.AvailableSwapAssetListResponse
import com.algorand.android.module.swap.component.data.model.PeraFeeResponse
import com.algorand.android.module.swap.component.data.model.SwapQuoteAssetDetailResponse
import com.algorand.android.module.swap.component.data.model.SwapQuoteProviderResponse
import com.algorand.android.module.swap.component.data.model.SwapQuoteRequestBody
import com.algorand.android.module.swap.component.data.model.SwapQuoteResponse
import com.algorand.android.module.swap.component.data.model.SwapQuoteTransactionResponse
import com.algorand.android.module.swap.component.data.model.SwapTypeResponse
import com.algorand.android.module.swap.component.domain.model.AvailableSwapAsset
import com.algorand.android.module.swap.component.domain.model.GetSwapQuoteRequestPayload
import com.algorand.android.module.swap.component.domain.model.PeraFee
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.model.SwapQuoteAssetDetail
import com.algorand.android.module.swap.component.domain.model.SwapQuoteProvider
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransactionResult
import com.algorand.android.module.swap.component.domain.model.SwapType
import java.math.BigDecimal
import java.math.BigInteger

internal interface PeraSwapFeeMapper {
    operator fun invoke(response: PeraFeeResponse): PeraFee
}

internal interface SwapQuoteTransactionMapper {
    operator fun invoke(response: SwapQuoteTransactionResponse): SwapQuoteTransactionResult
}

internal interface SwapQuoteRequestBodyMapper {
    operator fun invoke(payload: GetSwapQuoteRequestPayload): SwapQuoteRequestBody
}

internal interface SwapQuoteMapper {
    operator fun invoke(response: SwapQuoteResponse?): SwapQuote?
}

internal interface SwapTypeMapper {
    operator fun invoke(response: SwapTypeResponse?): SwapType
}

internal interface SwapQuoteProviderMapper {
    operator fun invoke(response: SwapQuoteProviderResponse?): SwapQuoteProvider
}

internal interface SwapQuoteAssetDetailMapper {
    operator fun invoke(response: SwapQuoteAssetDetailResponse?): SwapQuoteAssetDetail?
}

interface DisplayedCurrencyParityValueMapper {
    operator fun invoke(
        assetAmount: BigInteger,
        assetUsdValue: BigDecimal,
        assetDecimal: Int,
        assetId: Long
    ): ParityValue
}

internal interface AvailableSwapAssetsMapper {
    operator fun invoke(response: AvailableSwapAssetListResponse): List<AvailableSwapAsset>
}
