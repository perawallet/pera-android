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

package com.algorand.wallet.swap.data.mapper

import com.algorand.wallet.swap.data.model.AvailableSwapAssetResponse
import com.algorand.wallet.swap.data.model.SwapHistoryResponse
import com.algorand.wallet.swap.data.model.SwapHistoryStatusResponse
import com.algorand.wallet.swap.data.model.SwapPairHistoryResponse
import com.algorand.wallet.swap.data.model.SwapQuoteAssetDetailResponse
import com.algorand.wallet.swap.data.model.SwapQuoteProviderResponse
import com.algorand.wallet.swap.data.model.SwapQuoteRequestBody
import com.algorand.wallet.swap.data.model.SwapQuoteResponse
import com.algorand.wallet.swap.data.model.SwapQuoteTransactionResponse
import com.algorand.wallet.swap.data.model.SwapSelectedAssetDto
import com.algorand.wallet.swap.data.model.SwapTransactionPurposeResponse
import com.algorand.wallet.swap.data.model.SwapUpdateStatusRequestBody
import com.algorand.wallet.swap.data.model.TopSwapPairsResponse
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import com.algorand.wallet.swap.domain.model.SwapHistory
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus
import com.algorand.wallet.swap.domain.model.SwapPairHistory
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuoteTransaction
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import com.algorand.wallet.swap.domain.model.SwapTransactionPurpose
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import com.algorand.wallet.transaction.domain.model.TransactionId

internal fun interface SwapQuoteMapper {
    operator fun invoke(response: SwapQuoteResponse, providers: List<SwapQuoteProvider>): SwapQuoteV2?
}

internal fun interface SwapQuoteRequestBodyMapper {
    operator fun invoke(payload: SwapQuoteRequestPayload): SwapQuoteRequestBody
}

internal fun interface SwapQuoteTransactionMapper {
    operator fun invoke(response: SwapQuoteTransactionResponse): SwapQuoteTransaction?
}

internal fun interface SwapTransactionPurposeMapper {
    operator fun invoke(response: SwapTransactionPurposeResponse?): SwapTransactionPurpose
}

internal fun interface SwapQuoteProviderMapper {
    operator fun invoke(response: SwapQuoteProviderResponse): SwapQuoteProvider?
}

internal interface SwapAssetAmountMapper {
    fun mapAssetInAmount(response: SwapQuoteResponse): SwapQuoteV2.AssetAmount?
    fun mapAssetOutAmount(response: SwapQuoteResponse): SwapQuoteV2.AssetAmount?
}

internal fun interface SwapAssetDetailMapper {
    operator fun invoke(response: SwapQuoteAssetDetailResponse?): SwapQuoteV2.AssetDetail?
}

internal fun interface SwapSelectedAssetDetailMapper {
    operator fun invoke(dto: SwapSelectedAssetDto): SwapSelectedAssetDetail?
}

internal fun interface AvailableSwapAssetMapper {
    operator fun invoke(response: AvailableSwapAssetResponse): AvailableSwapAsset?
}

internal fun interface TopSwapPairsMapper {
    operator fun invoke(response: TopSwapPairsResponse): TopSwapPairs
}

internal fun interface SwapHistoryMapper {
    operator fun invoke(response: SwapHistoryResponse): SwapHistory?
}

internal interface SwapHistoryStatusMapper {
    operator fun invoke(response: SwapHistoryStatusResponse): SwapHistoryStatus
    operator fun invoke(status: SwapHistoryStatus): SwapHistoryStatusResponse
}

internal fun interface SwapPairHistoryMapper {
    operator fun invoke(response: SwapPairHistoryResponse): SwapPairHistory?
}

internal interface SwapUpdateStatusRequestBodyMapper {
    fun mapToInProgress(txnIds: List<TransactionId>): SwapUpdateStatusRequestBody
    fun mapToFailed(reason: SwapStatusFailureReason): SwapUpdateStatusRequestBody
}
