package com.algorand.android.asb.component.backupprotocol.model

import com.google.gson.annotations.SerializedName

internal data class BackUpProtocolPayload(
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("provider_name") val providerName: String?,
    @SerializedName("accounts") val accounts: List<BackUpProtocolElement>?
)
