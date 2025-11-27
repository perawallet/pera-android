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
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.CAN_OPEN_URI
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.CLOSE_WEB_VIEW
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_ADDRESSES
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_SETTINGS
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.LOG_ANALYTICS_EVENT
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.NOTIFY_USER
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.OPEN_NATIVE_URI
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.OPEN_SYSTEM_BROWSER
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.PUSH_WEB_VIEW
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
        processWebEvent(PUSH_WEB_VIEW) { mapper.mapPushWebViewEvent(params) }
    }

    @JavascriptInterface
    fun openSystemBrowser(params: String) {
        processWebEvent(OPEN_SYSTEM_BROWSER) { mapper.mapOpenSystemBrowserEvent(params) }
    }

    @JavascriptInterface
    fun canOpenURI(params: String) {
        processWebEvent(CAN_OPEN_URI) { mapper.mapCanOpenUriEvent(params) }
    }

    @JavascriptInterface
    fun openNativeURI(params: String) {
        processWebEvent(OPEN_NATIVE_URI) { mapper.mapOpenNativeUriEvent(params) }
    }

    @JavascriptInterface
    fun notifyUser(params: String) {
        processWebEvent(NOTIFY_USER) { mapper.mapNotifyUserEvent(params) }
    }

    @JavascriptInterface
    fun getAddresses() {
        processWebEvent(GET_ADDRESSES) { EventType.GetAddresses }
    }

    @JavascriptInterface
    fun getSettings() {
        processWebEvent(GET_SETTINGS) { EventType.GetSettings }
    }

    @JavascriptInterface
    fun logAnalyticsEvent(params: String) {
        processWebEvent(LOG_ANALYTICS_EVENT) { mapper.mapLogAnalyticsEvent(params) }
    }

    @JavascriptInterface
    fun closeWebView() {
        processWebEvent(CLOSE_WEB_VIEW) { EventType.CloseWebView }
    }

    private fun processWebEvent(name: String, onEventType: () -> EventType?) {
        val eventType = onEventType() ?: return
        val event = PeraInternalWebInterfaceEvent(name, eventType)
        onEvent?.invoke(event)
    }
}
