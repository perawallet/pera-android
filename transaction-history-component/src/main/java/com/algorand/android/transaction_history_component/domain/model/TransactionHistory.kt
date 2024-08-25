package com.algorand.android.transaction_history_component.domain.model

import java.math.BigInteger

data class TransactionHistory(
    val assetTransfer: AssetTransferHistory?,
    val assetConfiguration: AssetConfigurationHistory?,
    val applicationCall: ApplicationCallHistory?,
    val closeAmount: BigInteger?,
    val confirmedRound: Long?,
    val signature: Signature?,
    val fee: Long?,
    val id: String?,
    val senderAddress: String?,
    val payment: PaymentHistory?,
    val assetFreezeTransaction: AssetFreezeHistory?,
    val noteInBase64: String?,
    val roundTimeAsTimestamp: Long?,
    val rekeyTo: String?,
    val transactionType: TransactionType?,
    val innerTransactions: List<TransactionHistory>?,
    val createdAssetIndex: Long?,
    val keyRegTransaction: KeyRegTransactionHistory?
)
