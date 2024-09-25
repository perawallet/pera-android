package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName

internal data class SignatureResponse(
    @SerializedName("sig")
    val signatureKey: String?
)
