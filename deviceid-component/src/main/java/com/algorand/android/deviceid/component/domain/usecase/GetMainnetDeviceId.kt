package com.algorand.android.deviceid.component.domain.usecase

fun interface GetMainnetDeviceId {
    operator fun invoke(): String?
}
