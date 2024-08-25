package com.algorand.android.deviceid.component.domain.usecase

fun interface GetTestnetDeviceId {
    operator fun invoke(): String?
}
