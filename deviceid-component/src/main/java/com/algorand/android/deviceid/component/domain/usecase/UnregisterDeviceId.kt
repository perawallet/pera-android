package com.algorand.android.deviceid.component.domain.usecase

import com.algorand.android.foundation.PeraResult

fun interface UnregisterDeviceId {
    suspend operator fun invoke(deviceId: String): PeraResult<Unit>
}
