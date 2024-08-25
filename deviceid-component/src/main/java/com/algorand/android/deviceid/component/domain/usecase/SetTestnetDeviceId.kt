package com.algorand.android.deviceid.component.domain.usecase

fun interface SetTestnetDeviceId {
    operator fun invoke(deviceId: String?)
}
