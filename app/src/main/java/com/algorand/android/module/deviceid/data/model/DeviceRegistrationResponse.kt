package com.algorand.android.module.deviceid.data.model

import com.google.gson.annotations.SerializedName

internal data class DeviceRegistrationResponse(
    @SerializedName("id")
    val userId: String?
)
