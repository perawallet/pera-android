package com.algorand.android.transaction_history_component.data.repository

import com.algorand.android.assetdetail.component.AssetConstants
import com.algorand.android.network_utils.requestOld
import com.algorand.android.transaction_history_component.data.mapper.PaginatedTransactionMapper
import com.algorand.android.transaction_history_component.data.service.TransactionHistoryApiService
import com.algorand.android.transaction_history_component.domain.model.PaginatedTransactions
import com.algorand.android.transaction_history_component.domain.repository.TransactionHistoryRepository
import javax.inject.Inject

internal class TransactionHistoryRepositoryImpl @Inject constructor(
    private val transactionHistoryApiService: TransactionHistoryApiService,
    private val paginatedTransactionMapper: PaginatedTransactionMapper
) : TransactionHistoryRepository {

    override suspend fun getTransactionHistory(
        assetId: Long?,
        publicKey: String,
        fromDate: String?,
        toDate: String?,
        nextToken: String?,
        limit: Int?,
        txnType: String?
    ): Result<PaginatedTransactions> {
        return requestOld {
            val safeAssetId = if (assetId == AssetConstants.ALGO_ASSET_ID) null else assetId
            transactionHistoryApiService.getTransactionHistory(
                publicKey = publicKey,
                assetId = safeAssetId,
                afterTime = fromDate,
                beforeTime = toDate,
                nextToken = nextToken,
                limit = limit,
                transactionType = txnType
            )
        }.run {
            when {
                isSuccess -> Result.success(paginatedTransactionMapper(getOrThrow()))
                else -> {
                    // TODO recordException(exception)
                    Result.failure(exceptionOrNull() ?: Exception())
                }
            }
        }
    }
}
