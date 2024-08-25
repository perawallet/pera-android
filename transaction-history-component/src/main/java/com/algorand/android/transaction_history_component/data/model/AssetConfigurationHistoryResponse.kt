package com.algorand.android.transaction_history_component.data.model

import com.google.gson.annotations.SerializedName

internal data class AssetConfigurationHistoryResponse(
    @SerializedName("asset-id")
    val assetId: Long?,
    @SerializedName("params")
    val params: AssetConfigurationParamsResponse?
)