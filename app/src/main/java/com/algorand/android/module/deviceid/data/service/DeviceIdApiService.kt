package com.algorand.android.module.deviceid.data.service

import com.algorand.android.module.deviceid.data.model.DeviceRegistrationRequest
import com.algorand.android.module.deviceid.data.model.DeviceRegistrationResponse
import com.algorand.android.module.deviceid.data.model.DeviceUpdateRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
