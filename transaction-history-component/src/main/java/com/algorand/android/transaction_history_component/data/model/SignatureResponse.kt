package com.algorand.android.transaction_history_component.data.model

import com.google.gson.annotations.SerializedName

internal data class SignatureResponse(
    @SerializedName("sig")
    val signatureKey: String?
)
