package com.algorand.android.deviceid.component.data.model

import com.google.gson.annotations.SerializedName

internal data class DeviceUpdateRequest(
    @SerializedName("id") val id: String,
    @SerializedName("push_token") val pushToken: String,
    @SerializedName("accounts") val accountPublicKeys: List<String>,
    @SerializedName("application") val application: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("locale") val locale: String
)
