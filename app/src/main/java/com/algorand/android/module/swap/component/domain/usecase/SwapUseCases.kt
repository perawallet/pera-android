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

package com.algorand.android.module.swap.component.domain.usecase

import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.swap.component.domain.model.AvailableSwapAsset
import com.algorand.android.module.swap.component.domain.model.GetPercentageCalculatedBalanceForSwapPayload
import com.algorand.android.module.swap.component.domain.model.GetSwapQuotePayload
import com.algorand.android.module.swap.component.domain.model.PeraFee
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import java.math.BigDecimal
import java.math.BigInteger

interface GetSwapPeraFee {
    suspend operator fun invoke(fromAssetId: Long, amount: BigDecimal, fractionDecimals: Int): PeraResult<PeraFee>
}

interface GetPercentageCalculatedBalanceForSwap {
    suspend operator fun invoke(payload: GetPercentageCalculatedBalanceForSwapPayload): PeraResult<BigDecimal>
}

interface GetSwapQuote {
    suspend operator fun invoke(payload: GetSwapQuotePayload): PeraResult<SwapQuote>
}

fun interface GetAvailableTargetSwapAssets {
    suspend operator fun invoke(assetId: Long, query: String?): PeraResult<List<AvailableSwapAsset>>
}

fun interface RecordSwapQuoteException {
    suspend operator fun invoke(quoteId: Long, exceptionText: String?)
}

interface CreateSwapQuoteTransactions {
    suspend operator fun invoke(quoteId: Long, accountAddress: String): PeraResult<List<SwapQuoteTransaction>>
}

internal interface GetBalancePercentageForAlgo {
    suspend operator fun invoke(
        accountAlgoBalance: BigInteger,
        minRequiredBalance: BigInteger,
        percentage: BigDecimal
    ): PeraResult<BigDecimal>
}

internal interface GetBalancePercentageForAsset {
    suspend operator fun invoke(
        accountAlgoBalance: BigInteger,
        minRequiredBalance: BigInteger,
        accountAddress: String,
        fromAssetId: Long,
        percentage: BigDecimal,
        toAssetId: Long
    ): PeraResult<BigDecimal>
}

interface GetSwapAlgorandTransactionFee {
    operator fun invoke(transactions: Array<SwapQuoteTransaction>): Long
}

interface GetSwapOptInTransactionFee {
    operator fun invoke(transactions: Array<SwapQuoteTransaction>): Long
}
