package com.algorand.wallet.account.info.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class AssetHoldingNodeResponsePayload(
    @SerializedName("amount")
    val amount: BigInteger?,
    @SerializedName("asset-id")
    val assetId: Long?
)
