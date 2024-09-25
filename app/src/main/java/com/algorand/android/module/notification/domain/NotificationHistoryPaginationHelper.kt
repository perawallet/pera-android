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

package com.algorand.android.module.notification.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.algorand.android.module.notification.data.paging.NotificationHistoryDataSource
import com.algorand.android.module.notification.domain.model.NotificationHistory
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class NotificationHistoryPaginationHelper @Inject constructor() {

    private var dataSource: NotificationHistoryDataSource? = null

    private val pagingConfig = PagingConfig(pageSize = DEFAULT_NOTIFICATION_COUNT)

    var notificationHistoryPaginationFlow: Flow<PagingData<NotificationHistory>>? = null

    fun fetchNotificationHistory(
        onLoad: suspend (nextKey: String?) -> PagingSource.LoadResult<String, NotificationHistory>
    ) {
        if (notificationHistoryPaginationFlow == null) {
            notificationHistoryPaginationFlow = Pager(pagingConfig) {
                NotificationHistoryDataSource(onLoad).also { dataSource = it }
            }.flow
        } else {
            refreshNotificationHistoryData()
        }
    }

    fun refreshNotificationHistoryData() {
        dataSource?.invalidate()
    }

    private companion object {
        const val DEFAULT_NOTIFICATION_COUNT = 15
    }
}
