package com.algorand.android.deviceid.component.data.service

import com.algorand.android.deviceid.component.data.model.*
import retrofit2.http.*

internal interface DeviceIdApiService {

    @POST("v1/devices/")
    suspend fun postRegisterDevice(
        @Body deviceRegistrationRequest: DeviceRegistrationRequest
    ): DeviceRegistrationResponse

    @PUT("v1/devices/{device_id}/")
    suspend fun putUpdateDevice(
        @Path("device_id") deviceId: String,
        @Body deviceUpdateRequest: DeviceUpdateRequest
    ): DeviceRegistrationResponse
}
