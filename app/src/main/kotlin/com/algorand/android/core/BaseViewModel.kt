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

package com.algorand.android.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject
import kotlinx.coroutines.launch

open class BaseViewModel @Inject constructor() : ViewModel() {
    companion object {
        @JvmStatic
        private var eventTracker: PeraEventTracker? = null

        fun initialize(tracker: PeraEventTracker) {
            eventTracker = tracker
        }
    }

    fun logEvent(eventName: String) {
        if (eventTracker == null) {
            Log.e("PeraEventTracker", "Event tracker not initialized. Event '$eventName' not logged.")
            return
        }

        viewModelScope.launch {
            eventTracker?.logEvent(eventName)
        }
    }

    fun logEvent(eventName: String, params: Map<String, Any>) {
        if (eventTracker == null) {
            Log.e("PeraEventTracker", "Event tracker not initialized. Event '$eventName' with params not logged.")
            return
        }

        viewModelScope.launch {
            eventTracker?.logEvent(eventName, params)
        }
    }
}
