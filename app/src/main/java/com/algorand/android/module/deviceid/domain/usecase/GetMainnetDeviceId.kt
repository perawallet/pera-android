package com.algorand.android.module.deviceid.domain.usecase

fun interface GetMainnetDeviceId {
    operator fun invoke(): String?
}
