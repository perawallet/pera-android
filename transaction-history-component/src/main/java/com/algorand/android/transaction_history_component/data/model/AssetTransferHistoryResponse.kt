package com.algorand.android.transaction_history_component.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class AssetTransferHistoryResponse(
    @SerializedName("asset-id")
    val assetId: Long?,
    @SerializedName("amount")
    val amount: BigInteger?,
    @SerializedName("receiver")
    val receiverAddress: String?,
    @SerializedName("close-to")
    val closeTo: String?
)
