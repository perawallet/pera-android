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

package com.algorand.android.ui.webview.bridge

import android.webkit.JavascriptInterface
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceEventMapper
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult

@Suppress("unused")
internal class PeraWebViewPublicJsBridge(
    private val mapper: PeraWebInterfaceEventMapper,
    val onEvent: (List<PeraWebInterfaceEventResult>) -> Unit
) : PeraWebViewJsBridge {

    @JavascriptInterface
    fun handleRequest(params: String) {
        val requests = mapper.mapRequests(params)
        val filteredRequests = getFilteredRequests(requests)
        onEvent(filteredRequests)
    }

    private fun getFilteredRequests(requests: List<PeraWebInterfaceEventResult>): List<PeraWebInterfaceEventResult> {
        return requests.filter {
            if (it.result is PeraWebInterfaceEventResult.Result.Success) {
                it.result.event is PeraPublicWebInterfaceEvent
            } else {
                true
            }
        }
    }
}
