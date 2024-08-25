package com.algorand.android.nameservice.data.model

import com.google.gson.annotations.SerializedName

internal data class SearchNameServiceResponse(
    @SerializedName("results")
    val results: List<NameServicePayload>?
)
