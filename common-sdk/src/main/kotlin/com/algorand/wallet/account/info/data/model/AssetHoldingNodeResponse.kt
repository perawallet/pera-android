package com.algorand.wallet.account.info.data.model

import com.google.gson.annotations.SerializedName

internal data class AssetHoldingNodeResponse(
    @SerializedName("asset-holding")
    val assetHolding: AssetHoldingNodeResponsePayload?,
    @SerializedName("round")
    val round: Long?
)
