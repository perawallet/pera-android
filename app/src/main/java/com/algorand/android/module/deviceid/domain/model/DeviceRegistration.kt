package com.algorand.android.module.deviceid.domain.model

data class DeviceRegistration(
    val pushToken: String,
    val accountPublicKeys: List<String>
)
