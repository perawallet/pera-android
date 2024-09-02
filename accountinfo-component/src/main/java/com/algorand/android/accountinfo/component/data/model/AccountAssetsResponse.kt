package com.algorand.android.accountinfo.component.data.model

import com.google.gson.annotations.SerializedName

internal data class AccountAssetsResponse(
    @SerializedName("assets")
    val assets: List<AssetHoldingResponse>?,
    @SerializedName("current-round")
    val currentRound: Long?,
    @SerializedName("next-token")
    val nextToken: String?
)
