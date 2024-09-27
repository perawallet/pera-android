package com.algorand.android.module.deviceid.domain.usecase

import com.algorand.android.module.deviceid.domain.model.DeviceUpdate
import com.algorand.android.module.foundation.PeraResult

fun interface UpdateDeviceId {
    suspend operator fun invoke(deviceUpdate: DeviceUpdate): PeraResult<String>
}
