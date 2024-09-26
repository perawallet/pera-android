package com.algorand.android.module.deviceid.data.repository

import com.algorand.android.module.caching.SharedPrefLocalSource
import com.algorand.android.module.deviceid.data.service.DeviceIdApiService
import com.algorand.android.module.deviceid.domain.model.*
import com.algorand.android.module.deviceid.domain.repository.DeviceIdRepository
import com.algorand.android.module.deviceid.utils.ProvideDeviceIdPlatform
import com.algorand.android.foundation.PeraResult
import com.algorand.android.foundation.app.ProvideApplicationName
import com.algorand.android.foundation.locale.LocaleProvider

internal class DeviceIdRepositoryImpl(
    private val mainnetDeviceIdLocalSource: SharedPrefLocalSource<String?>,
    private val testnetDeviceIdLocalSource: SharedPrefLocalSource<String?>,
    private val notificationUserIdLocalSource: SharedPrefLocalSource<String?>,
    private val localeProvider: LocaleProvider,
    private val deviceIdApiService: DeviceIdApiService,
    private val provideApplicationName: ProvideApplicationName,
    private val provideDeviceIdPlatform: ProvideDeviceIdPlatform
) : DeviceIdRepository {

    override fun setMainnetDeviceId(deviceId: String?) {
        mainnetDeviceIdLocalSource.saveData(deviceId)
    }

    override fun getMainnetDeviceId(): String? {
        return mainnetDeviceIdLocalSource.getDataOrNull()
    }

    override fun setTestnetDeviceId(deviceId: String?) {
        testnetDeviceIdLocalSource.saveData(deviceId)
    }

    override fun getTestnetDeviceId(): String? {
        return testnetDeviceIdLocalSource.getDataOrNull()
    }

    override suspend fun registerDeviceId(deviceRegistration: DeviceRegistration): PeraResult<String> {
        return try {
            val payload = getDeviceRegistrationRequestPayload(deviceRegistration)
            val response = deviceIdApiService.postRegisterDevice(payload)
            PeraResult.Success(response.userId.orEmpty())
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun updateDeviceId(deviceUpdate: DeviceUpdate): PeraResult<String> {
        return try {
            val payload = getDeviceUpdateRequestPayload(deviceUpdate)
            val response = deviceIdApiService.putUpdateDevice(deviceUpdate.deviceId, payload)
            PeraResult.Success(response.userId.orEmpty())
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private fun getDeviceRegistrationRequestPayload(deviceRegistration: DeviceRegistration): com.algorand.android.module.deviceid.data.model.DeviceRegistrationRequest {
        return with(deviceRegistration) {
            com.algorand.android.module.deviceid.data.model.DeviceRegistrationRequest(
                pushToken = pushToken,
                application = provideApplicationName(),
                accountPublicKeys = accountPublicKeys,
                platform = provideDeviceIdPlatform(),
                locale = getLocaleLanguage()
            )
        }
    }

    private fun getDeviceUpdateRequestPayload(deviceUpdate: DeviceUpdate): com.algorand.android.module.deviceid.data.model.DeviceUpdateRequest {
        return with(deviceUpdate) {
            com.algorand.android.module.deviceid.data.model.DeviceUpdateRequest(
                id = deviceId,
                pushToken = pushToken.orEmpty(),
                application = provideApplicationName(),
                accountPublicKeys = accountPublicKeys,
                platform = provideDeviceIdPlatform(),
                locale = getLocaleLanguage()
            )
        }
    }

    override fun setNotificationUserId(deviceId: String?) {
        notificationUserIdLocalSource.saveData(deviceId)
    }

    override fun getNotificationUserId(): String? {
        return notificationUserIdLocalSource.getDataOrNull()
    }

    private fun getLocaleLanguage(): String = localeProvider.getLocale().language
}
