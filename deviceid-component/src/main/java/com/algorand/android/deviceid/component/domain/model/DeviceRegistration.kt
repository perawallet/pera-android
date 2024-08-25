package com.algorand.android.deviceid.component.domain.model

data class DeviceRegistration(
    val pushToken: String,
    val accountPublicKeys: List<String>
)
