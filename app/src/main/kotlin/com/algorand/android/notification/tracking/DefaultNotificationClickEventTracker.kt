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

import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject

internal class DefaultNotificationClickEventTracker @Inject constructor(
    private val eventTracker: PeraEventTracker
) : NotificationClickEventTracker {

    override suspend fun log(url: String) {
        eventTracker.logEvent(EVENT_NAME, mapOf(NOTIFICATION_ID_KEY to url))
    }

    override suspend fun log(notificationId: Long) {
        eventTracker.logEvent(EVENT_NAME, mapOf(NOTIFICATION_ID_KEY to notificationId))
    }

    private companion object {
        const val EVENT_NAME = "notification_open"
        const val NOTIFICATION_ID_KEY = "notification_id"
    }
}
