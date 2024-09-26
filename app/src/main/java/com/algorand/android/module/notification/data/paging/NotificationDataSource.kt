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

package com.algorand.android.module.notification.data.paging

import com.algorand.android.module.deviceid.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.foundation.PeraResult
import com.algorand.android.models.Pagination
import com.algorand.android.module.notification.domain.exception.MissingNotificationUserIdException
import com.algorand.android.module.notification.domain.model.NotificationHistory
import com.algorand.android.module.notification.domain.repository.NotificationRepository
import com.algorand.android.utils.PeraPagingSource

internal class NotificationDataSource(
    private val notificationRepository: NotificationRepository,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId
) : PeraPagingSource<String, NotificationHistory>() {

    override val logTag: String = NotificationDataSource::class.java.simpleName

    private var notificationUserId: String? = null

    override suspend fun initializeData(): LoadResult<String, NotificationHistory> {
        val notificationUserId = getNotificationUserId()
        return if (notificationUserId.isNullOrBlank()) {
//            recordException(MissingNotificationUserIdException) TODO
            LoadResult.Error(MissingNotificationUserIdException)
        } else {
            val result = notificationRepository.getNotificationHistory(notificationUserId)
            parseResult(result)
        }
    }

    override suspend fun loadMore(loadUrl: String): LoadResult<String, NotificationHistory> {
        val result = notificationRepository.getNotificationHistoryMore(loadUrl)
        return parseResult(result)
    }

    private fun parseResult(result: PeraResult<Pagination<NotificationHistory>>): LoadResult<String, NotificationHistory> {
        return when (result) {
            is PeraResult.Success -> {
                val notificationHistory = result.data.results
                val nextKey = result.data.next
                LoadResult.Page(data = notificationHistory, prevKey = null, nextKey = nextKey)
            }
            is PeraResult.Error -> {
                LoadResult.Error(result.exception)
            }
        }
    }

    private fun getNotificationUserId(): String? {
        return notificationUserId ?: run {
            getSelectedNodeDeviceId()?.also { newNotificationUserId ->
                notificationUserId = newNotificationUserId
            }
        }
    }
}
