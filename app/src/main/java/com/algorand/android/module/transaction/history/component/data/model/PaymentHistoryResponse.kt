package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class PaymentHistoryResponse(
    @SerializedName("amount")
    val amount: BigInteger,
    @SerializedName("receiver")
    val receiverAddress: String?,
    @SerializedName("close-amount")
    val closeAmount: BigInteger?,
    @SerializedName("close-remainder-to")
    val closeToAddress: String?
)
