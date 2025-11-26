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
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType

@Suppress("unused")
internal class PeraWebViewInternalBridge(
    private val mapper: PeraWebInterfaceEventMapper,
    var onEvent: ((event: PeraInternalWebInterfaceEvent) -> Unit)? = null
) : PeraWebViewJsBridge {

    @JavascriptInterface
    fun pushWebView(params: String) {
        processWebEvent("pushWebView") { mapper.mapPushWebViewEvent(params) }
    }

    @JavascriptInterface
    fun openSystemBrowser(params: String) {
        processWebEvent("openSystemBrowser") { mapper.mapOpenSystemBrowserEvent(params) }
    }

    @JavascriptInterface
    fun canOpenURI(params: String) {
        processWebEvent("canOpenURI") { mapper.mapCanOpenUriEvent(params) }
    }

    @JavascriptInterface
    fun openNativeURI(params: String) {
        processWebEvent("openNativeURI") { mapper.mapOpenNativeUriEvent(params) }
    }

    @JavascriptInterface
    fun notifyUser(params: String) {
        processWebEvent("notifyUser") { mapper.mapNotifyUserEvent(params) }
    }

    @JavascriptInterface
    fun getAddresses() {
        processWebEvent("getAddresses") { EventType.GetAddresses }
    }

    @JavascriptInterface
    fun getSettings() {
        processWebEvent("getSettings") { EventType.GetSettings }
    }

    @JavascriptInterface
    fun logAnalyticsEvent(params: String) {
        processWebEvent("logAnalyticsEvent") { mapper.mapLogAnalyticsEvent(params) }
    }

    @JavascriptInterface
    fun closeWebView() {
        processWebEvent("closeWebView") { EventType.CloseWebView }
    }

    private fun processWebEvent(name: String, onEventType: () -> EventType?) {
        val eventType = onEventType() ?: return
        val event = PeraInternalWebInterfaceEvent(name, eventType)
        onEvent?.invoke(event)
    }
}
