package com.algorand.android.module.transaction.history.component.data.repository

import com.algorand.android.module.asset.detail.component.AssetConstants
import com.algorand.android.module.network.requestOld
import com.algorand.android.module.transaction.history.component.data.mapper.PaginatedTransactionMapper
import com.algorand.android.module.transaction.history.component.data.service.TransactionHistoryApiService
import com.algorand.android.module.transaction.history.component.domain.model.PaginatedTransactions
import com.algorand.android.module.transaction.history.component.domain.repository.TransactionHistoryRepository
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
