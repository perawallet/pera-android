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

package com.algorand.wallet.analytics.tracking.domain.tracker

import com.algorand.wallet.analytics.tracking.domain.repository.PeraAnalyticsRepository
import com.algorand.wallet.analytics.tracking.domain.usecase.GetEventNameForSelectedNode
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class DefaultPeraAnalyticsEventTracker @Inject constructor(
    private val peraAnalyticsRepository: PeraAnalyticsRepository,
    private val getEventNameForSelectedNode: GetEventNameForSelectedNode
) : PeraAnalyticsEventTracker {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun logEvent(eventName: String) {
        coroutineScope.launch {
            val normalizedName = getEventNameForSelectedNode(eventName)
            peraAnalyticsRepository.logEvent(normalizedName)
        }
    }

    override fun logEvent(eventName: String, payloadMap: Map<String, Any>) {
        coroutineScope.launch {
            val normalizedName = getEventNameForSelectedNode(eventName)
            peraAnalyticsRepository.logEvent(normalizedName, payloadMap)
        }
    }
}
