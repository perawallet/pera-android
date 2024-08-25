package com.algorand.android.deviceid.component.utils

internal interface ProvideDeviceIdPlatform {
    operator fun invoke(): String
}
