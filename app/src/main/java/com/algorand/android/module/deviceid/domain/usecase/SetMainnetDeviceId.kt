package com.algorand.android.module.deviceid.domain.usecase

fun interface SetMainnetDeviceId {
    operator fun invoke(deviceId: String?)
}
