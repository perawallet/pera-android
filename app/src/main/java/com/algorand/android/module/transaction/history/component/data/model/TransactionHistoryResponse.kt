package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class TransactionHistoryResponse(
    @SerializedName("asset-transfer-transaction")
    val assetTransfer: AssetTransferHistoryResponse?,

    @SerializedName("asset-config-transaction")
    val assetConfiguration: AssetConfigurationHistoryResponse?,

    @SerializedName("application-transaction")
    val applicationCall: ApplicationCallResponse?,

    @SerializedName("closing-amount")
    val closeAmount: BigInteger?,

    @SerializedName("confirmed-round")
    val confirmedRound: Long?,

    @SerializedName("signature")
    val signature: SignatureResponse?,

    @SerializedName("fee")
    val fee: Long?,

    @SerializedName("id")
    val id: String?,

    @SerializedName("sender")
    val senderAddress: String?,

    @SerializedName("payment-transaction")
    val payment: PaymentHistoryResponse?,

    @SerializedName("asset-freeze-transaction")
    val assetFreezeTransaction: AssetFreezeHistoryResponse?,

    @SerializedName("note")
    val noteInBase64: String?,

    @SerializedName("round-time")
    val roundTimeAsTimestamp: Long?,

    @SerializedName("rekey-to")
    val rekeyTo: String?,

    @SerializedName("tx-type")
    val transactionType: TransactionTypeResponse?,

    @SerializedName("inner-txns")
    val innerTransactions: List<TransactionHistoryResponse>?,

    @SerializedName("created-asset-index")
    val createdAssetIndex: Long?,

    @SerializedName("keyreg-transaction")
    val keyRegTransaction: KeyRegTransactionHistoryResponse?
)
