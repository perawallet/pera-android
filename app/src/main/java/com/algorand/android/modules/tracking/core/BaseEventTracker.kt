/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.tracking.core

import com.algorand.android.node.domain.usecase.IsSelectedNodeTestnet
import javax.inject.Inject

open class BaseEventTracker protected constructor(private val peraEventTracker: PeraEventTracker) {
    // TODO handle event tracking permission level in this class

    @Inject
    protected lateinit var isSelectedNodeTestnet: IsSelectedNodeTestnet

    protected suspend fun logEvent(eventName: String) {
        peraEventTracker.logEvent(getFormattedEventName(eventName))
    }

    protected suspend fun logEvent(eventName: String, payloadMap: Map<String, Any>) {
        peraEventTracker.logEvent(eventName, payloadMap)
    }

    private suspend fun getFormattedEventName(eventName: String): String {
        return if (isSelectedNodeTestnet()) "$TESTNET_EVENT_NAME_PREFIX$eventName" else eventName
    }

    companion object {
        private const val TESTNET_EVENT_NAME_PREFIX = "t_"
    }
}
