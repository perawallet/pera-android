package com.algorand.android.deviceid.component.domain.repository

import com.algorand.android.deviceid.component.domain.model.*
import com.algorand.android.foundation.PeraResult

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
