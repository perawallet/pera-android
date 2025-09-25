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

package com.algorand.wallet.swap.domain.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import com.algorand.wallet.swap.domain.model.SwapPeraFee
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuoteTransaction
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import java.math.BigInteger

internal interface SwapRepository {
    suspend fun getLastUsedSwapAddress(): String?
    suspend fun setLastUsedSwapAddress(address: String)
    suspend fun getSwapQuotes(payload: SwapQuoteRequestPayload): PeraResult<List<SwapQuoteV2>>
    suspend fun getPeraFee(assetInId: Long, amount: BigInteger): PeraResult<SwapPeraFee>
    suspend fun createQuoteTransactions(quoteId: Long): PeraResult<List<SwapQuoteTransaction>>
    suspend fun getAvailableAssetsToSwap(assetInId: Long, query: String?): PeraResult<List<AvailableSwapAsset>>
    suspend fun getTopSwapPairs(): PeraResult<TopSwapPairs>
    suspend fun getUseLocalCurrencyPreference(): Boolean
    suspend fun setUseLocalCurrencyPreference(useLocalCurrency: Boolean)
    suspend fun setSwapStatusFailed(quoteId: Long, reason: SwapStatusFailureReason)
    suspend fun setSwapStatusInProgress(quoteId: Long)
}
