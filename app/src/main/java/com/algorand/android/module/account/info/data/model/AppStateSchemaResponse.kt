package com.algorand.android.module.account.info.data.model

import com.google.gson.annotations.SerializedName

internal data class AppStateSchemaResponse(
    @SerializedName("num-byte-slice")
    val numByteSlice: Long?,
    @SerializedName("num-uint")
    val numUint: Long?
)
