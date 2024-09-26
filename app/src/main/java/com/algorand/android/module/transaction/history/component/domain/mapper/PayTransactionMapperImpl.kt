package com.algorand.android.module.transaction.history.component.domain.mapper

import com.algorand.android.module.date.timeStampToZonedDateTime
import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.module.transaction.history.component.domain.model.TransactionHistory
import com.algorand.android.module.transaction.history.component.domain.utils.TransactionUtils.isReceivingTransaction
import com.algorand.android.module.transaction.history.component.domain.utils.TransactionUtils.isSelfTransaction
import java.math.BigInteger
import javax.inject.Inject

internal class PayTransactionMapperImpl @Inject constructor() : PayTransactionMapper {

    override fun invoke(transaction: TransactionHistory, address: String): BaseTransactionHistory {
        val closeToAddress = transaction.payment?.closeToAddress
        val receiverAddress = transaction.payment?.receiverAddress
        val senderAddress = transaction.senderAddress
        return when {
            isSelfTransaction(address, senderAddress, receiverAddress) -> createSelfPayTransaction(transaction)
            isReceivingTransaction(address, closeToAddress, receiverAddress) -> {
                createReceivingPayTransaction(transaction)
            }
            else -> createSendPaymentTransaction(transaction)
        }
    }

    private fun createSelfPayTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return BaseTransactionHistory(
            id = transaction.id,
            signature = transaction.signature?.signatureKey,
            senderAddress = transaction.senderAddress,
            receiverAddress = transaction.payment?.receiverAddress,
            zonedDateTime = timeStampToZonedDateTime(transaction.roundTimeAsTimestamp ?: 0),
            isPending = false,
            roundTimeAsTimestamp = transaction.roundTimeAsTimestamp ?: 0L,
            type = BaseTransactionHistory.BaseTransactionType.Pay.Self(
                amount = transaction.payment?.amount ?: BigInteger.ZERO
            ),
        )
    }

    private fun createReceivingPayTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return BaseTransactionHistory(
            id = transaction.id,
            signature = transaction.signature?.signatureKey,
            senderAddress = transaction.senderAddress,
            receiverAddress = transaction.payment?.receiverAddress,
            zonedDateTime = timeStampToZonedDateTime(transaction.roundTimeAsTimestamp ?: 0),
            isPending = false,
            roundTimeAsTimestamp = transaction.roundTimeAsTimestamp ?: 0L,
            type = BaseTransactionHistory.BaseTransactionType.Pay.Receive(
                amount = transaction.payment?.amount ?: BigInteger.ZERO
            ),
        )
    }

    private fun createSendPaymentTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return BaseTransactionHistory(
            id = transaction.id,
            signature = transaction.signature?.signatureKey,
            senderAddress = transaction.senderAddress,
            receiverAddress = transaction.payment?.receiverAddress,
            zonedDateTime = timeStampToZonedDateTime(transaction.roundTimeAsTimestamp ?: 0),
            isPending = false,
            roundTimeAsTimestamp = transaction.roundTimeAsTimestamp ?: 0L,
            type = BaseTransactionHistory.BaseTransactionType.Pay.Send(
                amount = transaction.payment?.amount ?: BigInteger.ZERO
            ),
        )
    }
}