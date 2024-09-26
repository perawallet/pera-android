package com.algorand.android.module.nameservice.data.model

import com.google.gson.annotations.SerializedName

internal data class NameServiceResponse(
    @SerializedName("name")
    val name: String?,

    @SerializedName("source")
    val source: NameServiceSourceResponse?,

    @SerializedName("image")
    val imageUri: String?
)
