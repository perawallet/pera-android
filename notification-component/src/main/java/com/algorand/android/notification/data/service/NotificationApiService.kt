/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.notification.data.service

import com.algorand.android.notification.data.model.NotificationFilterRequest
import com.algorand.android.notification.data.model.NotificationHistoryResponse
import com.algorand.android.pagination.component.model.Pagination
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Url

internal interface NotificationApiService {

    @PATCH("v1/devices/{device_id}/accounts/{address}/")
    suspend fun putNotificationFilter(
        @Path("device_id") deviceId: String,
        @Path("address") address: String,
        @Body notificationFilterRequest: NotificationFilterRequest
    ): Response<Unit>

    @GET("v2/devices/{device_id}/notifications/")
    suspend fun getNotifications(
        @Path("device_id") deviceId: String
    ): Response<Pagination<NotificationHistoryResponse>>

    @GET
    suspend fun getNotificationsMore(@Url url: String): Response<Pagination<NotificationHistoryResponse>>
}
