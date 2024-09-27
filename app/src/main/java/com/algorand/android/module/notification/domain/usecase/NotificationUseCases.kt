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
import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.notification.domain.model.NotificationHistory
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow

fun interface GetNotificationLastRefreshDateTime {
    operator fun invoke(): ZonedDateTime
}

interface SetNotificationLastRefreshDateTime {
    operator fun invoke(dateTime: ZonedDateTime)
}

interface GetNotificationHistoryPagingFlow {
    operator fun invoke(): Flow<PagingData<NotificationHistory>>?

    fun refresh()
}

fun interface DeleteNotificationFilterByAddress {
    suspend operator fun invoke(address: String)
}

interface SetNotificationFilter {
    suspend operator fun invoke(address: String, isFiltered: Boolean): PeraResult<Unit>
}
