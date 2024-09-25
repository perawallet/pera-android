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

package com.algorand.android.module.notification.domain.repository

import com.algorand.android.foundation.PeraResult
import com.algorand.android.models.Pagination
import com.algorand.android.module.notification.domain.model.NotificationHistory

internal interface NotificationRepository {

    suspend fun setNotificationFilter(address: String, isFiltered: Boolean, deviceId: String): PeraResult<Unit>

    suspend fun deleteFilterByAddress(address: String)

    fun setNotificationLastRefreshDateTime(dateTime: String)

    fun getNotificationLastRefreshDateTime(): String?

    suspend fun getNotificationHistory(userId: String): PeraResult<Pagination<NotificationHistory>>

    suspend fun getNotificationHistoryMore(nextUrl: String): PeraResult<Pagination<NotificationHistory>>
}
