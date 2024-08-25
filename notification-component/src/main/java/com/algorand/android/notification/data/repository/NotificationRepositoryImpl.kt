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

package com.algorand.android.notification.data.repository

import com.algorand.android.foundation.PeraResult
import com.algorand.android.network_utils.requestWithHipoErrorHandler
import com.algorand.android.notification.data.mapper.NotificationHistoryMapper
import com.algorand.android.notification.data.model.NotificationFilterRequest
import com.algorand.android.notification.data.service.NotificationApiService
import com.algorand.android.notification.data.storage.NotificationRefreshTimeLocalSource
import com.algorand.android.notification.domain.model.NotificationHistory
import com.algorand.android.notification.domain.repository.NotificationRepository
import com.algorand.android.pagination.component.model.Pagination
import com.algorand.android.shared_db.notification.dao.NotificationFilterDao
import com.algorand.android.shared_db.notification.model.NotificationFilterEntity
import com.hipo.hipoexceptionsandroid.RetrofitErrorHandler
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationRefreshTimeLocalSource: NotificationRefreshTimeLocalSource,
    private val notificationFilterDao: NotificationFilterDao,
    private val hipoApiErrorHandler: RetrofitErrorHandler,
    private val notificationApiService: NotificationApiService,
    private val notificationHistoryMapper: NotificationHistoryMapper
) : NotificationRepository {

    override suspend fun setNotificationFilter(
        address: String,
        isFiltered: Boolean,
        deviceId: String
    ): PeraResult<Unit> {
        addFilterToDatabase(address, isFiltered)
        return registerNotificationFilter(address, isFiltered, deviceId).use(
            onSuccess = {
                PeraResult.Success(it)
            },
            onFailed = { exception, code ->
                addFilterToDatabase(address, isFiltered.not())
                PeraResult.Error(exception, code)
            }
        )
    }

    override fun setNotificationLastRefreshDateTime(dateTime: String) {
        notificationRefreshTimeLocalSource.saveData(dateTime)
    }

    override fun getNotificationLastRefreshDateTime(): String? {
        return notificationRefreshTimeLocalSource.getDataOrNull()
    }

    override suspend fun deleteFilterByAddress(address: String) {
        notificationFilterDao.deleteByAddress(address)
    }

    override suspend fun getNotificationHistory(userId: String): PeraResult<Pagination<NotificationHistory>> {
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            notificationApiService.getNotifications(userId)
        }.map {
            val historyList = it.results.mapNotNull { notificationHistoryMapper(it) }
            Pagination(it.next, historyList)
        }
    }

    override suspend fun getNotificationHistoryMore(nextUrl: String): PeraResult<Pagination<NotificationHistory>> {
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            notificationApiService.getNotificationsMore(nextUrl)
        }.map {
            val historyList = it.results.mapNotNull { notificationHistoryMapper(it) }
            Pagination(it.next, historyList)
        }
    }

    private suspend fun addFilterToDatabase(address: String, isFiltered: Boolean) {
        if (isFiltered) {
            notificationFilterDao.insert(NotificationFilterEntity(address))
        } else {
            notificationFilterDao.deleteByAddress(address)
        }
    }

    private suspend fun registerNotificationFilter(
        address: String,
        isFiltered: Boolean,
        deviceId: String
    ): PeraResult<Unit> {
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            val request = NotificationFilterRequest(!isFiltered)
            notificationApiService.putNotificationFilter(deviceId, address, request)
        }
    }
}
