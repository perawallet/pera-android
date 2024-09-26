package com.algorand.android.module.asset.detail.component.asset.data.model

import com.google.gson.annotations.SerializedName

internal data class Pagination<T>(
    @SerializedName("next") val next: String?,
    @SerializedName("results") val results: List<T>
)