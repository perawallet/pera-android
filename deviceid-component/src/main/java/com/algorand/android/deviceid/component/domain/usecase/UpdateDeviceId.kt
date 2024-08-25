package com.algorand.android.deviceid.component.domain.usecase

import com.algorand.android.deviceid.component.domain.model.DeviceUpdate

fun interface UpdateDeviceId {
    suspend operator fun invoke(deviceUpdate: DeviceUpdate): Result<String>
}
