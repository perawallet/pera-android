package com.algorand.android.deviceid.component.domain.usecase

import com.algorand.android.deviceid.component.domain.model.DeviceUpdate
import com.algorand.android.foundation.PeraResult

fun interface UpdateDeviceId {
    suspend operator fun invoke(deviceUpdate: DeviceUpdate): PeraResult<String>
}
