package com.algorand.android.deviceid.component.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.deviceid.component.domain.model.DeviceUpdate
import com.algorand.android.deviceid.component.domain.usecase.UnregisterDeviceId
import com.algorand.android.deviceid.component.domain.usecase.UpdateDeviceId
import com.algorand.android.foundation.PeraResult
import javax.inject.Inject

internal class UnregisterDeviceIdUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val updateDeviceId: UpdateDeviceId
) : UnregisterDeviceId {

    override suspend fun invoke(deviceId: String): PeraResult<Unit> {
        val accountAddresses = getLocalAccounts().map { it.address }
        return updateDeviceId(DeviceUpdate(deviceId, null, accountAddresses)).use(
            onSuccess = { PeraResult.Success(Unit) },
            onFailed = { exception, _ -> PeraResult.Error(exception) }
        )
    }
}
