package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName

internal data class AssetFreezeHistoryResponse(
    @SerializedName("address")
    val receiverAddress: String?,
    @SerializedName("asset-id")
    val assetId: Long?,
    @SerializedName("new-freeze-status")
    val newFreezeStatus: Boolean?
)
