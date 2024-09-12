package com.algorand.android.network_utils.exceptions

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class BaseError(
    @SerializedName("type") val type: String? = null,
    @SerializedName("detail") val detail: JsonElement? = null,
    @SerializedName("fallback_message") val fallbackMessage: String? = null
)
