package com.algorand.android.deviceregistration.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.deviceid.component.domain.model.DeviceUpdate
import com.algorand.android.deviceid.component.domain.usecase.UpdateDeviceId
import javax.inject.Inject

class UpdatePushTokenUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val updateDeviceId: UpdateDeviceId
) {

    suspend operator fun invoke(deviceId: String, token: String?): Result<String> {
        val accountAddresses = getLocalAccounts().map { it.address }
        return updateDeviceId(DeviceUpdate(deviceId, token, accountAddresses))
    }
}
