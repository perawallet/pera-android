package com.algorand.android.module.deviceid.utils

import javax.inject.Inject

internal class ProvideDeviceIdPlatformImpl @Inject constructor() : ProvideDeviceIdPlatform {

    override fun invoke(): String {
        return "android"
    }
}
