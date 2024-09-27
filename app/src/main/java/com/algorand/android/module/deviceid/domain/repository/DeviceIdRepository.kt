package com.algorand.android.module.deviceid.domain.repository

import com.algorand.android.module.deviceid.domain.model.DeviceRegistration
import com.algorand.android.module.deviceid.domain.model.DeviceUpdate
import com.algorand.android.module.foundation.PeraResult

internal interface DeviceIdRepository {

    fun setMainnetDeviceId(deviceId: String?)

    fun getMainnetDeviceId(): String?

    fun setTestnetDeviceId(deviceId: String?)

    fun getTestnetDeviceId(): String?

    suspend fun registerDeviceId(deviceRegistration: DeviceRegistration): PeraResult<String>

    suspend fun updateDeviceId(deviceUpdate: DeviceUpdate): PeraResult<String>

    /**
     * Functions below (NotificationUserId) are being used to support previously held user device IDs.
     * @see NotificationUserIdLocalSource
     */
    fun setNotificationUserId(deviceId: String?)
    fun getNotificationUserId(): String?
}
