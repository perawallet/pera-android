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
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_PUBLIC_SETTINGS
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent

@Suppress("unused")
internal class PeraWebViewPublicJsBridge(
    val onEvent: ((event: PeraPublicWebInterfaceEvent) -> Unit)? = null
) : PeraWebViewJsBridge {

    @JavascriptInterface
    fun getPublicSettings(params: Any) {
        val event = PeraPublicWebInterfaceEvent(
            name = GET_PUBLIC_SETTINGS,
            eventType = PeraPublicWebInterfaceEvent.EventType.GetPublicSettings
        )
        onEvent?.invoke(event)
    }
}
