package com.algorand.android.module.nameservice.data.model

import com.google.gson.annotations.SerializedName

internal data class SearchNameServiceResponse(
    @SerializedName("results")
    val results: List<NameServicePayload>?
)
