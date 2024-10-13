package com.algorand.android.modules.perawebview

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import javax.inject.Inject

class GetDeviceIdWebMessageUseCase @Inject constructor(
    private val deviceIdUseCase: DeviceIdUseCase,
    private val peraWebMessageBuilder: PeraWebMessageBuilder
) : GetDeviceIdWebMessage {

    override fun invoke(): String? {
        val deviceId = deviceIdUseCase.getSelectedNodeDeviceId() ?: return null
        return peraWebMessageBuilder.buildMessage(PeraWebMessageAction.GET_DEVICE_ID, deviceId)
    }
}
