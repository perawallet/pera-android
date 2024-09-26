package com.algorand.android.module.account.info.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class AssetHoldingResponse(
    @SerializedName("asset-id")
    val assetId: Long?,

    @SerializedName("amount")
    val amount: BigInteger?,

    @SerializedName("deleted")
    val isDeleted: Boolean?,

    @SerializedName("is-frozen")
    val isFrozen: Boolean?,

    @SerializedName("opted-in-at-round")
    val optedInAtRound: Long?,

    @SerializedName("opted-out-at-round")
    val optedOutAtRound: Long?
)
