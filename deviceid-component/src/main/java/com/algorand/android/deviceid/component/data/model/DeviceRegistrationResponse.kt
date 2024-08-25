package com.algorand.android.deviceid.component.data.model

import com.google.gson.annotations.SerializedName

internal data class DeviceRegistrationResponse(
    @SerializedName("id")
    val userId: String?
)
