package com.algorand.android.deviceid.component.domain.model

data class DeviceUpdate(
    val deviceId: String,
    val pushToken: String?,
    val accountPublicKeys: List<String>
)
