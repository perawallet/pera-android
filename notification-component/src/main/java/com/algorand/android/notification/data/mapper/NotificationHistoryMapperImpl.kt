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

package com.algorand.android.notification.data.mapper

import com.algorand.android.notification.data.model.NotificationHistoryResponse
import com.algorand.android.notification.data.model.NotificationIconResponse
import com.algorand.android.notification.data.model.NotificationIconShapeResponse
import com.algorand.android.notification.domain.model.NotificationHistory
import com.algorand.android.notification.domain.model.NotificationIcon
import com.algorand.android.notification.domain.model.NotificationIconShape
import javax.inject.Inject

internal class NotificationHistoryMapperImpl @Inject constructor() : NotificationHistoryMapper {

    override fun invoke(response: NotificationHistoryResponse): NotificationHistory? {
        return NotificationHistory(
            id = response.id ?: return null,
            accountAddress = response.accountAddress,
            message = response.message,
            url = response.url,
            creationDatetime = response.creationDatetime,
            isUnread = response.isUnread,
            icon = mapToIcon(response.icon)
        )
    }

    private fun mapToIcon(response: NotificationIconResponse?): NotificationIcon {
        return NotificationIcon(
            prismUrl = response?.prismUrl,
            shape = mapToIconShape(response?.shape)
        )
    }

    private fun mapToIconShape(response: NotificationIconShapeResponse?): NotificationIconShape? {
        return when (response) {
            NotificationIconShapeResponse.CIRCLE -> NotificationIconShape.CIRCLE
            NotificationIconShapeResponse.RECTANGLE -> NotificationIconShape.RECTANGLE
            null -> null
        }
    }
}
