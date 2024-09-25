package com.algorand.android.module.transaction.history.component.data.mapper.implementation

import com.algorand.android.module.transaction.history.component.data.mapper.ApplicationCallHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.AssetConfigurationHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.AssetFreezeHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.AssetTransferHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.KeyRegTransactionHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.PaginatedTransactionMapper
import com.algorand.android.module.transaction.history.component.data.mapper.PaymentHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.SignatureMapper
import com.algorand.android.module.transaction.history.component.data.mapper.TransactionTypeMapper
import com.algorand.android.module.transaction.history.component.data.model.PaginatedTransactionsResponse
import com.algorand.android.module.transaction.history.component.data.model.TransactionHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.PaginatedTransactions
import com.algorand.android.module.transaction.history.component.domain.model.TransactionHistory
import javax.inject.Inject

internal class PaginatedTransactionMapperImpl @Inject constructor(
    private val assetTransferHistoryMapper: AssetTransferHistoryMapper,
    private val assetConfigurationHistoryMapper: AssetConfigurationHistoryMapper,
    private val applicationCallHistoryMapper: ApplicationCallHistoryMapper,
    private val signatureMapper: SignatureMapper,
    private val paymentHistoryMapper: PaymentHistoryMapper,
    private val assetFreezeHistoryMapper: AssetFreezeHistoryMapper,
    private val transactionTypeMapper: TransactionTypeMapper,
    private val keyRegTransactionHistoryMapper: KeyRegTransactionHistoryMapper
) : PaginatedTransactionMapper {

    override fun invoke(response: PaginatedTransactionsResponse): PaginatedTransactions {
        return PaginatedTransactions(
            nextToken = response.nextToken,
            transactionList = createTransactionHistoryList(response.transactionList)
        )
    }

    private fun createTransactionHistoryList(responseList: List<TransactionHistoryResponse>): List<TransactionHistory> {
        return responseList.map {
            TransactionHistory(
                assetTransfer = assetTransferHistoryMapper(it.assetTransfer),
                assetConfiguration = assetConfigurationHistoryMapper(it.assetConfiguration),
                applicationCall = applicationCallHistoryMapper(it.applicationCall),
                closeAmount = it.closeAmount,
                confirmedRound = it.confirmedRound,
                signature = signatureMapper(it.signature),
                fee = it.fee,
                id = it.id,
                senderAddress = it.senderAddress,
                payment = paymentHistoryMapper(it.payment),
                assetFreezeTransaction = assetFreezeHistoryMapper(it.assetFreezeTransaction),
                noteInBase64 = it.noteInBase64,
                roundTimeAsTimestamp = it.roundTimeAsTimestamp,
                rekeyTo = it.rekeyTo,
                transactionType = transactionTypeMapper(it.transactionType),
                innerTransactions = createTransactionHistoryList(it.innerTransactions ?: listOf()),
                createdAssetIndex = it.createdAssetIndex,
                keyRegTransaction = keyRegTransactionHistoryMapper(it.keyRegTransaction)
            )
        }
    }
}
