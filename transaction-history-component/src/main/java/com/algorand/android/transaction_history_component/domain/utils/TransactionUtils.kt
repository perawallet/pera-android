package com.algorand.android.transaction_history_component.domain.utils

import com.algorand.android.transaction_history_component.domain.model.TransactionHistory
import java.math.BigInteger

internal object TransactionUtils {

    fun isSelfTransaction(accountPublicKey: String, senderAddress: String?, receiverAddress: String?): Boolean {
        return senderAddress == accountPublicKey && receiverAddress == accountPublicKey
    }

    fun isReceivingTransaction(
        accountPublicKey: String,
        closeToAddress: String?,
        receiverAddress: String?
    ): Boolean {
        return receiverAddress == accountPublicKey || closeToAddress == accountPublicKey
    }

    fun isSelfOptInTransaction(
        accountPublicKey: String,
        senderAddress: String?,
        receiverAddress: String?,
        amount: BigInteger
    ): Boolean {
        return isSelfTransaction(accountPublicKey, senderAddress, receiverAddress) && amount == BigInteger.ZERO
    }

    fun getAllNestedTransactions(transaction: TransactionHistory): Sequence<TransactionHistory> {
        return sequence {
            if (transaction.innerTransactions != null) {
                yieldAll(transaction.innerTransactions)
                transaction.innerTransactions.forEach { yieldAll(getAllNestedTransactions(it)) }
            }
        }
    }
}
