package com.algorand.android.module.deviceid.domain.usecase

import com.algorand.android.foundation.PeraResult

fun interface UnregisterDeviceId {
    suspend operator fun invoke(deviceId: String): PeraResult<Unit>
}
