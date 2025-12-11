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

package com.algorand.wallet.transaction.history.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistorySwapGroupDetailMapper
import com.algorand.wallet.transaction.history.data.service.TransactionHistoryApiService
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistoryPagingData
import com.algorand.wallet.transaction.history.domain.model.TransactionHistorySwapGroupDetail
import com.algorand.wallet.transaction.history.domain.repository.TransactionHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DefaultTransactionHistoryRepository @Inject constructor(
    private val transactionHistoryPagingSource: PagingSource<TransactionHistoryPagingData, TransactionHistory>,
    private val transactionHistoryApiService: TransactionHistoryApiService,
    private val swapGroupDetailMapper: TransactionHistorySwapGroupDetailMapper,
    private val errorLogger: PeraErrorLogger
) : TransactionHistoryRepository {

    override fun getTransactionHistory(data: TransactionHistoryPagingData): Flow<PagingData<TransactionHistory>> {
        return Pager(
            config = PagingConfig(pageSize = data.itemPerPage),
            initialKey = data,
            pagingSourceFactory = { transactionHistoryPagingSource }
        ).flow
    }

    override suspend fun getSwapGroupTransactions(
        address: String,
        groupId: String
    ): PeraResult<TransactionHistorySwapGroupDetail> {
        return try {
            val response = transactionHistoryApiService.getSwapGroupTransactions(address, groupId)
            val detail = swapGroupDetailMapper(address, response)
            if (detail != null) {
                PeraResult.Success(detail)
            } else {
                errorLogger.logError("$logTag - Failed to map swap group detail response for groupId: $groupId")
                PeraResult.Error(Exception())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private companion object {
        private val logTag = DefaultTransactionHistoryRepository::class.java.simpleName
    }
}
