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

package com.algorand.android.discover.utils

import com.algorand.android.discover.common.ui.model.WebViewTheme
import com.algorand.android.discover.home.domain.model.DappFavorite
import com.google.gson.Gson

@Suppress("MaxLineLength")
const val JAVASCRIPT_PERACONNECT = "function setupPeraConnectObserver(){const e=new MutationObserver(()=>{const t=document.getElementById(\"pera-wallet-connect-modal-wrapper\"),e=document.getElementById(\"pera-wallet-redirect-modal-wrapper\");if(e&&e.remove(),t){const o=t.getElementsByTagName(\"pera-wallet-connect-modal\");let e=\"\";if(o&&o[0]&&o[0].shadowRoot){const r=o[0].shadowRoot.querySelector(\"pera-wallet-modal-touch-screen-mode\").shadowRoot.querySelector(\"#pera-wallet-connect-modal-touch-screen-mode-launch-pera-wallet-button\");r&&(e=r.getAttribute(\"href\"))}else{const n=t.getElementsByClassName(\"pera-wallet-connect-modal-touch-screen-mode__launch-pera-wallet-button\");n&&(e=n[0].getAttribute(\"href\"))}e&&(e=e.replace(/&browser=\\w+/,\"\"),window.open(e)),t.remove()}});e.disconnect(),e.observe(document.body,{childList:!0,subtree:!0})}setupPeraConnectObserver();"
const val JAVASCRIPT_NAVIGATION = "!function(t){function e(t){setTimeout((function(){window.webkit.messageHandlers.navigation.postMessage(t)}),0)}function n(n){return function(){return e(\"other\"),n.apply(t,arguments)}}t.pushState=n(t.pushState),t.replaceState=n(t.replaceState),window.addEventListener(\"popstate\",(function(){e(\"backforward\")}))}(window.history);"
private const val BROWSER_FAVORITE_BUTTON_CLICK_ACTION = "handleBrowserFavoriteButtonClick"
private const val GET_DEVICE_ID_ACTION = "getDeviceId"

fun getJavascriptThemeChangeFunctionForTheme(theme: WebViewTheme): String {
    return "updateTheme('" + theme.key + "');"
}

fun getAddToFavoriteFunction(favorite: DappFavorite, gson: Gson): String {
    return getHandleMessageWithStringifiedPayload(
        DiscoverWebMessage(
            action = BROWSER_FAVORITE_BUTTON_CLICK_ACTION,
            payload = favorite
        ),
        gson
    )
}

fun getSendDeviceId(deviceId: String, gson: Gson): String {
    return getHandleMessageWithStringifiedPayload(
        DiscoverWebMessage(
            action = GET_DEVICE_ID_ACTION,
            payload = deviceId
        ),
        gson
    )
}

private fun getHandleMessageWithStringifiedPayload(payload: DiscoverWebMessage? = null, gson: Gson): String {
    return "handleMessage('" + gson.toJson(payload) + "')"
}
