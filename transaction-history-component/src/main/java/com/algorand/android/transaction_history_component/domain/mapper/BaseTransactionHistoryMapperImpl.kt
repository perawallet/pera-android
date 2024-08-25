package com.algorand.android.transaction_history_component.domain.mapper

import com.algorand.android.date.component.timeStampToZonedDateTime
import com.algorand.android.transaction_history_component.domain.model.*
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.transaction_history_component.domain.model.TransactionType.*
import com.algorand.android.transaction_history_component.domain.utils.TransactionUtils.getAllNestedTransactions
import javax.inject.Inject

internal class BaseTransactionHistoryMapperImpl @Inject constructor(
    private val payTransactionMapper: PayTransactionMapper,
    private val assetTransactionMapper: AssetTransactionMapper,
    private val keyRegTransactionMapper: KeyRegTransactionMapper
) : BaseTransactionHistoryMapper {

    override fun invoke(
        address: String,
        transactionHistoryList: List<TransactionHistory>
    ): List<BaseTransactionHistory> {
        return transactionHistoryList.mapNotNull { txn ->
            when (txn.transactionType) {
                PAY_TRANSACTION -> payTransactionMapper(txn, address)
                ASSET_TRANSACTION -> assetTransactionMapper(txn, address)
                ASSET_CONFIGURATION -> createAssetConfigurationTransaction(txn)
                APP_TRANSACTION -> createApplicationCallTransaction(txn)
                KEYREG_TRANSACTION -> keyRegTransactionMapper(txn)
                else -> createUndefinedTransaction(txn)
            }
        }
    }

    override fun mapToTransactionDateTitle(title: String): BaseTransactionHistoryItem {
        return BaseTransactionHistoryItem.TransactionDateTitle(title)
    }

    private fun createAssetConfigurationTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress,
                receiverAddress = null,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetConfiguration(
                    assetId = assetConfiguration?.assetId
                )
            )
        }
    }

    private fun createApplicationCallTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress,
                receiverAddress = null,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.ApplicationCall(
                    applicationId = applicationCall?.applicationId,
                    innerTransactionCount = getAllNestedTransactions(this).count(),
                    foreignAssetIds = applicationCall?.foreignAssets
                )
            )
        }
    }

    private fun createUndefinedTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress,
                receiverAddress = null,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.Undefined
            )
        }
    }
}
