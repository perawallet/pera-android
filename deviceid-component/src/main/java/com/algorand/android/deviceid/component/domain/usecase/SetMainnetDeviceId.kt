package com.algorand.android.deviceid.component.domain.usecase

fun interface SetMainnetDeviceId {
    operator fun invoke(deviceId: String?)
}
