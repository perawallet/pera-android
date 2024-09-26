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

package com.algorand.android.module.notification.domain.usecase

import androidx.paging.PagingData
import androidx.paging.PagingSource.LoadResult
import com.algorand.android.module.deviceid.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.module.notification.domain.NotificationHistoryPaginationHelper
import com.algorand.android.module.notification.domain.model.NotificationHistory
import com.algorand.android.module.notification.domain.repository.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class GetNotificationHistoryPagingFlowUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val notificationHistoryPaginationHelper: NotificationHistoryPaginationHelper,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId
) : GetNotificationHistoryPagingFlow {

    override fun invoke(): Flow<PagingData<NotificationHistory>>? {
        notificationHistoryPaginationHelper.fetchNotificationHistory { nextKey ->
            onLoadNotificationHistory(nextKey)
        }
        return notificationHistoryPaginationHelper.notificationHistoryPaginationFlow
    }

    override fun refresh() {
        notificationHistoryPaginationHelper.refreshNotificationHistoryData()
    }

    private suspend fun onLoadNotificationHistory(nextKey: String?): LoadResult<String, NotificationHistory> {
        return with(notificationRepository) {
            if (nextKey.isNullOrBlank()) {
                getNotificationHistory(getSelectedNodeDeviceId().orEmpty())
            } else {
                getNotificationHistoryMore(nextKey)
            }
        }.use(
            onSuccess = {
                LoadResult.Page(data = it.results, prevKey = null, nextKey = it.next)
            },
            onFailed = { exception, _ ->
                LoadResult.Error(exception)
            }
        )
    }
}
