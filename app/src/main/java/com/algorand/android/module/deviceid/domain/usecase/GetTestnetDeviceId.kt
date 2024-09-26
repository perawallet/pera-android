package com.algorand.android.module.deviceid.domain.usecase

fun interface GetTestnetDeviceId {
    operator fun invoke(): String?
}
