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

package com.algorand.android.modules.notification.ui.mapper

import com.algorand.android.modules.notification.ui.model.NotificationListItem
import com.algorand.android.modules.notification.ui.utils.NotificationIconDrawableProvider
import com.algorand.android.notification.domain.model.NotificationHistory
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate
import java.time.ZonedDateTime
import javax.inject.Inject

class NotificationListItemMapper @Inject constructor() {

    fun map(notificationHistory: NotificationHistory): NotificationListItem {
        val now = ZonedDateTime.now()
        val nowInTimeMillis = now.toInstant().toEpochMilli()
        val dateFormatter = getAlgorandMobileDateFormatter()
        val creationZonedDateTime = notificationHistory.creationDatetime.parseFormattedDate(dateFormatter) ?: now

        val timeDifference = nowInTimeMillis - creationZonedDateTime.toInstant().toEpochMilli()
        val isFailed = notificationHistory.url?.isBlank() ?: true
        return NotificationListItem(
            id = notificationHistory.id,
            uri = notificationHistory.url,
            isFailed = isFailed,
            creationDateTime = creationZonedDateTime,
            timeDifference = timeDifference,
            message = notificationHistory.message ?: "",
            notificationIconDrawableProvider = NotificationIconDrawableProvider.create(
                isFailed = isFailed,
                logoUri = notificationHistory.icon?.prismUrl
            )
        )
    }
}
