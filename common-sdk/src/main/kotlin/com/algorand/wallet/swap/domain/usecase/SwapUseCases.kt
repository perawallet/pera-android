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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import com.algorand.wallet.swap.domain.model.SwapAmountByPercentagePayload
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuotes
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import java.math.BigDecimal

fun interface GetSwapQuotes {
    suspend operator fun invoke(payload: SwapQuotePayload): PeraResult<SwapQuotes>
}

fun interface GetSelectedSwapAssetDetail {
    suspend operator fun invoke(address: String, assetId: Long): PeraResult<SwapSelectedAssetDetail>
}

fun interface GetPreselectedSwapAddress {
    suspend operator fun invoke(): String?
}

internal fun interface GetSwapQuoteDetails {
    suspend operator fun invoke(quotes: List<SwapQuoteV2>): List<SwapQuoteDetail>
}

fun interface GetAvailableSwapAssets {
    suspend operator fun invoke(assetInId: Long, query: String?): PeraResult<List<AvailableSwapAsset>>
}

fun interface GetSwapPeraFee {
    suspend operator fun invoke(assetInId: Long, amount: BigDecimal, fractionDecimals: Int): PeraResult<BigDecimal>
}

fun interface GetSwapAmountByPercentage {
    suspend operator fun invoke(payload: SwapAmountByPercentagePayload): PeraResult<BigDecimal>
}
