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

import androidx.paging.PagingData
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import com.algorand.wallet.swap.domain.model.SignedSwapTransaction
import com.algorand.wallet.swap.domain.model.SwapAmountByPercentagePayload
import com.algorand.wallet.swap.domain.model.SwapHistory
import com.algorand.wallet.swap.domain.model.SwapHistoryPagingData
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus
import com.algorand.wallet.swap.domain.model.SwapPairHistory
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuotes
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import com.algorand.wallet.transaction.domain.model.TransactionId
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

fun interface GetSwapQuotes {
    suspend operator fun invoke(payload: SwapQuotePayload): PeraResult<SwapQuotes>
}

fun interface GetSelectedSwapAssetDetail {
    suspend operator fun invoke(address: String, assetId: Long): PeraResult<SwapSelectedAssetDetail>
}

fun interface GetLastUsedSwapAddress {
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

fun interface GetTopSwapPairs {
    suspend operator fun invoke(): PeraResult<TopSwapPairs>
}

fun interface GetSwapHistory {
    operator fun invoke(data: SwapHistoryPagingData): Flow<PagingData<SwapHistory>>
}

fun interface GetSwapPairHistory {
    suspend operator fun invoke(address: String, statuses: List<SwapHistoryStatus>): PeraResult<List<SwapPairHistory>>
}

fun interface SetLastUsedSwapAddress {
    suspend operator fun invoke(address: String)
}

fun interface GetSwapUseLocalCurrencyPreference {
    suspend operator fun invoke(): Boolean
}

fun interface SetSwapUseLocalCurrencyPreference {
    suspend operator fun invoke(useLocalCurrency: Boolean)
}

fun interface SetSwapStatusInProgress {
    suspend operator fun invoke(swapId: Long, txnIds: List<TransactionId>)
}

fun interface SetSwapStatusFailed {
    suspend operator fun invoke(quoteId: Long, reason: SwapStatusFailureReason)
}

fun interface GetSwapFeePadding {
    operator fun invoke(): BigDecimal
}

fun interface SendSwapTransactions {
    suspend operator fun invoke(swapId: Long, txns: List<SignedSwapTransaction>): PeraResult<List<TransactionId>>
}

fun interface SetSwapSlippageTolerancePercentage {
    suspend operator fun invoke(percentage: Double?)
}

fun interface GetSwapSlippageTolerancePercentage {
    suspend operator fun invoke(): Double?
}
