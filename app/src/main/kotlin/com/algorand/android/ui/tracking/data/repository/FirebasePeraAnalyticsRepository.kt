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

package com.algorand.android.ui.tracking.data.repository

import com.algorand.android.ui.tracking.data.mapper.EventTrackingPayloadBundleMapper
import com.algorand.wallet.analytics.tracking.domain.repository.PeraAnalyticsRepository
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

internal class FirebasePeraAnalyticsRepository @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val payloadBundleMapper: EventTrackingPayloadBundleMapper
) : PeraAnalyticsRepository {

    override suspend fun logEvent(eventName: String) {
        firebaseAnalytics.logEvent(eventName, null)
    }

    override suspend fun logEvent(eventName: String, payloadMap: Map<String, Any>) {
        val payloadBundle = payloadBundleMapper(payloadMap)
        firebaseAnalytics.logEvent(eventName, payloadBundle)
    }
}
