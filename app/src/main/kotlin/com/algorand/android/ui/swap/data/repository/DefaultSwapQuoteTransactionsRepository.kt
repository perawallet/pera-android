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

package com.algorand.android.ui.swap.data.repository

import com.algorand.android.modules.swap.confirmswap.data.mapper.SwapQuoteTransactionDTOMapper
import com.algorand.android.modules.swap.confirmswap.data.model.CreateSwapQuoteTransactionsRequestBody
import com.algorand.android.modules.swap.confirmswap.data.model.CreateSwapQuoteTransactionsResponse
import com.algorand.android.ui.swap.data.network.SwapQuoteTransactionsApiService
import com.algorand.android.ui.swap.domain.model.SwapQuoteTransactionsDto
import com.algorand.android.ui.swap.domain.repository.SwapQuoteTransactionsRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.exceptions.peraApiError
import com.algorand.wallet.logger.PeraLogger
import com.google.gson.Gson
import javax.inject.Inject

internal class DefaultSwapQuoteTransactionsRepository @Inject constructor(
    private val swapQuoteTransactionsApiService: SwapQuoteTransactionsApiService,
    private val swapQuoteTransactionDTOMapper: SwapQuoteTransactionDTOMapper,
    private val gson: Gson
) : SwapQuoteTransactionsRepository {

    override suspend fun createQuoteTransactions(quoteId: Long): PeraResult<SwapQuoteTransactionsDto> {
        return try {
            val requestBody = CreateSwapQuoteTransactionsRequestBody(quoteId)
            val transactionsResponse = swapQuoteTransactionsApiService.getQuoteTransactions(requestBody)
            getSwapQuoteTransactionsResult(transactionsResponse)
        } catch (e: Exception) {
            PeraLogger.e(TAG, "API call failed for quoteId=$quoteId", e)
            peraApiError(e, gson)
        }
    }

    private fun getSwapQuoteTransactionsResult(
        response: CreateSwapQuoteTransactionsResponse
    ): PeraResult<SwapQuoteTransactionsDto> {
        val txns = response.transactionGroups?.map { swapQuoteTransactionDTOMapper.mapToSwapQuoteTransactionDTO(it) }
        return if (txns == null || txns.isEmpty() || response.swapId == null) {
            PeraLogger.e(
                TAG,
                "Invalid response - txnGroups=${response.transactionGroups?.size}, swapId=${response.swapId}"
            )
            PeraResult.Error(IllegalStateException("Invalid swap quote transactions response"))
        } else {
            PeraResult.Success(SwapQuoteTransactionsDto(txns, response.swapId))
        }
    }

    private companion object {
        const val TAG = "SwapQuoteTxnRepo"
    }
}
