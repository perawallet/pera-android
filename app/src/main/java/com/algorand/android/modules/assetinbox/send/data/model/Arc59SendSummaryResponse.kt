package com.algorand.android.modules.assetinbox.send.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

class Arc59SendSummaryResponse(
    @SerializedName("is_arc59_opted_in")
    val isArc59OptedIn: Boolean?,

    @SerializedName("minimum_balance_requirement")
    val minimumBalanceRequirement: Long?,

    @SerializedName("inner_tx_count")
    val innerTxCount: Int?,

    @SerializedName("total_protocol_and_mbr_fee")
    val totalProtocolAndMbrFee: BigInteger?,

    @SerializedName("inbox_address")
    val inboxAddress: String?,

    @SerializedName("algo_fund_amount")
    val algoFundAmount: BigInteger?
)
