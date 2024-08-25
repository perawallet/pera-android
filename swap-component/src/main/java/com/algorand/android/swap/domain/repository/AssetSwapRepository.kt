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

package com.algorand.android.swap.domain.repository

import com.algorand.android.foundation.PeraResult
import com.algorand.android.swap.domain.model.AvailableSwapAsset
import com.algorand.android.swap.domain.model.GetSwapQuoteRequestPayload
import com.algorand.android.swap.domain.model.PeraFee
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.model.swapquotetxns.SwapQuoteTransactionResult
import java.math.BigInteger

internal interface AssetSwapRepository {

    suspend fun getSwapQuote(payload: GetSwapQuoteRequestPayload): PeraResult<SwapQuote>

    suspend fun getPeraFee(fromAssetId: Long, amount: BigInteger): PeraResult<PeraFee>

    suspend fun createQuoteTransactions(quoteId: Long): PeraResult<List<SwapQuoteTransactionResult>>

    suspend fun recordSwapQuoteException(quoteId: Long, exceptionText: String?)

    suspend fun getAvailableTargetSwapAssets(assetId: Long, query: String?): PeraResult<List<AvailableSwapAsset>>
}
