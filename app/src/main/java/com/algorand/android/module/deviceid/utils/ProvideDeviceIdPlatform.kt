package com.algorand.android.module.deviceid.utils

internal interface ProvideDeviceIdPlatform {
    operator fun invoke(): String
}
