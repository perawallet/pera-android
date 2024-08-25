package com.algorand.android.transaction_history_component.domain.usecase.implementation

import androidx.paging.*
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.date.component.*
import com.algorand.android.transaction_history_component.data.util.TransactionHistoryPaginationHelper
import com.algorand.android.transaction_history_component.domain.mapper.BaseTransactionHistoryMapper
import com.algorand.android.transaction_history_component.domain.model.*
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.*
import com.algorand.android.transaction_history_component.domain.repository.TransactionHistoryRepository
import com.algorand.android.transaction_history_component.domain.usecase.GetTransactionPaginationFlow
import com.algorand.android.transaction_history_component.domain.utils.TransactionHistoryConstants.DEFAULT_TRANSACTION_TO_FETCH_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import java.time.ZonedDateTime
import javax.inject.Inject

internal class GetTransactionPaginationFlowUseCase @Inject constructor(
    private val transactionHistoryRepository: TransactionHistoryRepository,
    private val transactionHistoryPaginationHelper: TransactionHistoryPaginationHelper,
    private val baseTransactionHistoryMapper: BaseTransactionHistoryMapper,
    private val fetchAndCacheAssets: FetchAndCacheAssets
) : GetTransactionPaginationFlow {

    private val dateFilterQuery = MutableStateFlow<Pair<ZonedDateTime?, ZonedDateTime?>>(null to null)

    override fun invoke(
        address: String,
        assetIdFilter: Long?,
        coroutineScope: CoroutineScope,
        txnType: String?
    ): Flow<PagingData<BaseTransactionHistoryItem>>? {
        transactionHistoryPaginationHelper.fetchTransactionHistory(coroutineScope) { params ->
            val (fromDate, toDate) = dateFilterQuery.value
            onLoadTransactions(address, params, assetIdFilter, txnType, fromDate, toDate)
        }
        return transactionHistoryPaginationHelper.transactionPaginationFlow?.map { pagingData ->
            pagingData.insertSeparators { txn1: BaseTransactionHistory?, txn2: BaseTransactionHistory? ->
                if (shouldAddDateSeparator(txn1, txn2)) {
                    val transactionDate = txn2?.zonedDateTime?.formatAsDate()
                    baseTransactionHistoryMapper.mapToTransactionDateTitle(transactionDate.orEmpty())
                } else {
                    null
                }
            }
        }
    }

    override suspend fun filterHistoryByDate(fromDate: ZonedDateTime?, toDate: ZonedDateTime?) {
        dateFilterQuery.emit(fromDate to toDate)
        refreshTransactionHistory()
    }

    override fun refreshTransactionHistory() {
        transactionHistoryPaginationHelper.refreshTransactionHistoryData()
    }

    private suspend fun onLoadTransactions(
        address: String,
        nextKey: String?,
        assetId: Long? = null,
        txnType: String? = null,
        fromDate: ZonedDateTime?,
        toDate: ZonedDateTime?
    ): PagingSource.LoadResult<String, BaseTransactionHistory> {
        return try {
            val response = transactionHistoryRepository.getTransactionHistory(
                assetId = assetId,
                publicKey = address,
                fromDate = fromDate?.formatAsRFC3339Version(),
                toDate = toDate?.formatAsRFC3339Version(),
                nextToken = nextKey,
                txnType = txnType,
                limit = DEFAULT_TRANSACTION_TO_FETCH_COUNT
            )
            when {
                response.isSuccess -> {
                    getSuccessResult(address, response.getOrThrow(), fromDate, toDate)
                }
                else -> {
                    PagingSource.LoadResult.Error(response.exceptionOrNull()!!)
                }
            }
        } catch (exception: Exception) {
            PagingSource.LoadResult.Error(exception)
        }
    }

    private suspend fun getSuccessResult(
        address: String,
        paginatedTransactions: PaginatedTransactions,
        fromDate: ZonedDateTime?,
        toDate: ZonedDateTime?
    ): PagingSource.LoadResult<String, BaseTransactionHistory> {
        val dateFilteredTransactionList = getDateFilteredTransactionList(
            transactionList = paginatedTransactions.transactionList,
            fromDate = fromDate,
            toDate = toDate
        )
        val baseTransactionList = baseTransactionHistoryMapper(address, dateFilteredTransactionList)
        val assetIds = getAssetIdsFromTransactions(baseTransactionList)
        if (assetIds.isNotEmpty()) {
            fetchAndCacheAssets(assetIds.toList(), includeDeleted = true)
        }
        return PagingSource.LoadResult.Page(
            data = baseTransactionList,
            prevKey = null,
            nextKey = paginatedTransactions.nextToken,
        )
    }

    private fun getDateFilteredTransactionList(
        transactionList: List<TransactionHistory>,
        fromDate: ZonedDateTime?,
        toDate: ZonedDateTime?
    ): List<TransactionHistory> {
        return if (fromDate == null || toDate == null) {
            transactionList
        } else {
            transactionList.filter { txn ->
                val timestampAsZonedDateTime = if (txn.roundTimeAsTimestamp != null) {
                    timeStampToZonedDateTime(txn.roundTimeAsTimestamp)
                } else {
                    null
                }
                return@filter timestampAsZonedDateTime?.isAfter(fromDate) ?: true &&
                    timestampAsZonedDateTime?.isBefore(toDate) ?: true
            }
        }
    }

    private fun shouldAddDateSeparator(
        firstTxnItem: BaseTransactionHistory?,
        secondTxnItem: BaseTransactionHistory?
    ): Boolean {
        return when {
            secondTxnItem == null -> false
            firstTxnItem == null -> true
            firstTxnItem.zonedDateTime?.toLocalDate() == secondTxnItem.zonedDateTime?.toLocalDate() -> false
            else -> true
        }
    }

    private fun getAssetIdsFromTransactions(transactionList: List<BaseTransactionHistory>): Set<Long> {
        return mutableSetOf<Long>().apply {
            transactionList.forEach {
                when (it.type) {
                    is ApplicationCall -> addAll(it.type.foreignAssetIds.orEmpty())
                    is AssetConfiguration -> it.type.assetId?.let { safeId -> add(safeId) }
                    is AssetTransfer -> add(it.type.assetId)
                    else -> {
                        // TODO sendErrorLog("Unhandled else case in TransactionHistoryUseCase.getAssetIdsFromTransactions")
                    }
                }
            }
        }
    }
}
