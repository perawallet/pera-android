/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.notification.tracking

import com.algorand.android.modules.tracking.core.BaseEventTracker
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject

internal class DefaultNotificationClickEventTracker @Inject constructor(
    eventTracker: PeraEventTracker
) : BaseEventTracker(eventTracker), NotificationClickEventTracker {

    override suspend fun logPushNotificationClick(notificationType: String) {
        logEvent(PUSH_EVENT_NAME, mapOf(NOTIFICATION_TYPE_KEY to notificationType))
    }

    override suspend fun logNotificationCenterClick(notificationType: String) {
        logEvent(NOTIFICATION_CENTER_EVENT_NAME, mapOf(NOTIFICATION_TYPE_KEY to notificationType))
    }

    private companion object {
        const val PUSH_EVENT_NAME = "notification_open"
        const val NOTIFICATION_CENTER_EVENT_NAME = "notificationscr_open"
        const val NOTIFICATION_TYPE_KEY = "notification_type"
    }
}
