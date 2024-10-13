package com.algorand.android.modules.perawebview.model

import com.google.gson.annotations.SerializedName

data class PeraWebMessage(
    @SerializedName("action")
    val action: String,
    @SerializedName("payload")
    val payload: String
)
