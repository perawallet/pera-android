package com.algorand.android.module.deviceid.data.model

import com.google.gson.annotations.SerializedName

internal data class DeviceRegistrationRequest(
    @SerializedName("push_token") val pushToken: String,
    @SerializedName("accounts") val accountPublicKeys: List<String>,
    @SerializedName("application") val application: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("locale") val locale: String
)
