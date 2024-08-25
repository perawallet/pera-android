package com.algorand.android.deviceid.component.domain.usecase

import com.algorand.android.deviceid.component.domain.model.DeviceRegistration

fun interface RegisterDeviceId {
    suspend operator fun invoke(deviceRegistration: DeviceRegistration): Result<String>
}
