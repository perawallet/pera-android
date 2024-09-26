package com.algorand.android.module.deviceid.domain.model

data class DeviceUpdate(
    val deviceId: String,
    val pushToken: String?,
    val accountPublicKeys: List<String>
)
