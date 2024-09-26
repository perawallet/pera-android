package com.algorand.android.module.deviceid.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.deviceid.domain.model.DeviceRegistration
import com.algorand.android.module.deviceid.domain.model.DeviceUpdate
import com.algorand.android.module.deviceid.domain.repository.DeviceIdRepository
import com.algorand.android.module.deviceid.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.RegisterDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetSelectedNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.UpdateDeviceId
import com.algorand.android.foundation.PeraResult
import javax.inject.Inject
import kotlinx.coroutines.delay

internal class RegisterDeviceIdUseCase @Inject constructor(
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val setSelectedNodeDeviceId: SetSelectedNodeDeviceId,
    private val getLocalAccounts: GetLocalAccounts,
    private val deviceIdRepository: DeviceIdRepository,
    private val updateDeviceId: UpdateDeviceId
) : RegisterDeviceId {

    override suspend fun invoke(token: String): PeraResult<String> {
        return when (val deviceId = getSelectedNodeDeviceId()) {
            null -> registerDeviceId(token)
            else -> updateDeviceId(deviceId, token)
        }
    }

    private suspend fun registerDeviceId(token: String): PeraResult<String> {
        val accountAddresses = getLocalAccountAddresses()
        return deviceIdRepository.registerDeviceId(DeviceRegistration(token, accountAddresses)).use(
            onSuccess = { deviceId ->
                setSelectedNodeDeviceId(deviceId)
                PeraResult.Success(deviceId)
            },
            onFailed = { _, _ ->
                delay(REGISTER_DEVICE_FAIL_DELAY)
                invoke(token)
            }
        )
    }

    private suspend fun updateDeviceId(deviceId: String, token: String): PeraResult<String> {
        val accountAddresses = getLocalAccountAddresses()
        return updateDeviceId(DeviceUpdate(deviceId, token, accountAddresses))
    }

    private suspend fun getLocalAccountAddresses(): List<String> {
        return getLocalAccounts().map { it.address }
    }

    private companion object {
        const val REGISTER_DEVICE_FAIL_DELAY = 1500L
    }
}
