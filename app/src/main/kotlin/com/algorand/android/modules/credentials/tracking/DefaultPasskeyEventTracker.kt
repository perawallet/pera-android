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

package com.algorand.android.modules.credentials.tracking

import com.algorand.android.credentials.passkeys.ui.tracking.PasskeyEventTracker
import com.algorand.android.modules.tracking.core.BaseEventTracker
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject

internal class DefaultPasskeyEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), PasskeyEventTracker {

    override suspend fun logPasskeyCreated(appInfo: String) {
        logPasskeyEvent(PASSKEY_CREATED_EVENT_KEY, appInfo)
    }

    override suspend fun logPasskeyAuthenticated(appInfo: String) {
        logPasskeyEvent(PASSKEY_AUTHENTICATED_EVENT_KEY, appInfo)
    }

    private suspend fun logPasskeyEvent(eventKey: String, appInfo: String) {
        if (appInfo.isBlank()) {
            logEvent(eventKey)
        } else {
            val eventParams = mapOf(APP_INFO_KEY to appInfo)
            logEvent(eventKey, eventParams)
        }
    }

    private companion object {
        const val PASSKEY_CREATED_EVENT_KEY = "passkey_register"
        const val PASSKEY_AUTHENTICATED_EVENT_KEY = "passkey_authenticate"
        const val APP_INFO_KEY = "app_info"
    }
}
