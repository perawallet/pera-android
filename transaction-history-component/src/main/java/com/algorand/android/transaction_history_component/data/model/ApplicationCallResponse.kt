package com.algorand.android.transaction_history_component.data.model

import com.google.gson.annotations.SerializedName

data class ApplicationCallResponse(
    @SerializedName("application-id")
    val applicationId: Long?,
    @SerializedName("accounts")
    val accounts: List<String>?,
    @SerializedName("foreign-apps")
    val foreignApps: List<Long>?,
    @SerializedName("foreign-assets")
    val foreignAssets: List<Long>?,
    @SerializedName("on-completion")
    val onCompletion: OnCompletionResponse?,
)
