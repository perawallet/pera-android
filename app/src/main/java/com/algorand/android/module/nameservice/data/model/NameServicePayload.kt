package com.algorand.android.module.nameservice.data.model

import com.google.gson.annotations.SerializedName

internal data class NameServicePayload(
    @SerializedName("address")
    val address: String?,

    @SerializedName("name")
    val nameResponse: NameServiceResponse?
)
