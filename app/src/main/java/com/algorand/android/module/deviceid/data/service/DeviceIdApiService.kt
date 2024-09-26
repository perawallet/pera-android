package com.algorand.android.module.deviceid.data.service

import com.algorand.android.module.deviceid.data.model.*
import retrofit2.http.*

internal interface DeviceIdApiService {

    @POST("v1/devices/")
    suspend fun postRegisterDevice(
        @Body deviceRegistrationRequest: com.algorand.android.module.deviceid.data.model.DeviceRegistrationRequest
    ): com.algorand.android.module.deviceid.data.model.DeviceRegistrationResponse

    @PUT("v1/devices/{device_id}/")
    suspend fun putUpdateDevice(
        @Path("device_id") deviceId: String,
        @Body deviceUpdateRequest: com.algorand.android.module.deviceid.data.model.DeviceUpdateRequest
    ): com.algorand.android.module.deviceid.data.model.DeviceRegistrationResponse
}
