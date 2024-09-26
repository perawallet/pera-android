package com.algorand.android.module.deviceid.domain.usecase

fun interface SetTestnetDeviceId {
    operator fun invoke(deviceId: String?)
}
