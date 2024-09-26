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

package com.algorand.android.module.transaction.component.pendingtxn.data.repository

import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.foundation.PeraResult
import com.algorand.android.network_utils.safeApiCall
import com.algorand.android.module.transaction.component.pendingtxn.data.mapper.PendingTransactionMapper
import com.algorand.android.module.transaction.component.pendingtxn.data.model.PendingTransactionsResponse
import com.algorand.android.module.transaction.component.pendingtxn.data.service.PendingTransactionsApiService
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.PendingTransaction
import com.algorand.android.module.transaction.component.pendingtxn.domain.repository.PendingTransactionRepository
import javax.inject.Inject

internal class PendingTransactionRepositoryImpl @Inject constructor(
    private val pendingTransactionsApiService: PendingTransactionsApiService,
    private val pendingTransactionMapper: PendingTransactionMapper
) : PendingTransactionRepository {

    override suspend fun getPendingTransactions(address: String, assetId: Long?): PeraResult<List<PendingTransaction>> {
        return safeApiCall { requestGetPendingTransactions(address) }.map { pendingTransactionResponse ->
            getTransactionItems(pendingTransactionResponse, assetId)
        }
    }

    private fun getTransactionItems(response: PendingTransactionsResponse, assetId: Long?): List<PendingTransaction> {
        return response.pendingTransactionResponses?.filter {
            if (assetId == null) {
                true
            } else {
                val responseAssetId = it.detailResponse?.assetId ?: ALGO_ASSET_ID
                responseAssetId == assetId
            }
        }?.map {
            pendingTransactionMapper(it)
        }.orEmpty()
    }

    private suspend fun requestGetPendingTransactions(publicKey: String): PeraResult<PendingTransactionsResponse> {
        with(pendingTransactionsApiService.getPendingTransactions(publicKey)) {
            return if (isSuccessful && body() != null) {
                PeraResult.Success(body() as PendingTransactionsResponse)
            } else {
                PeraResult.Error(Exception(errorBody()?.charStream()?.readText()))
            }
        }
    }
}
