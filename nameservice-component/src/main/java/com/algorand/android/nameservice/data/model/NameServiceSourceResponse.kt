package com.algorand.android.nameservice.data.model

import com.google.gson.annotations.SerializedName

internal enum class NameServiceSourceResponse {
    @SerializedName("nfdomain")
    NFDOMAIN,

    UNKNOWN
}
