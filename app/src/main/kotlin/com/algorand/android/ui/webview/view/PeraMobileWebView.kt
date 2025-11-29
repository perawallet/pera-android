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

package com.algorand.android.ui.webview.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.CookieManager
import android.webkit.WebMessage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import com.algorand.android.BuildConfig
import com.algorand.android.R

class PeraMobileWebView : WebView {
    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        clearCookies()
        initSettings()
        setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, false)
        setBackgroundColor(context.getColor(R.color.background))
    }

    private fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSettings() {
        this.settings.javaScriptEnabled = true
        this.settings.domStorageEnabled = true
        this.settings.javaScriptCanOpenWindowsAutomatically = true
        this.settings.allowFileAccess = false
        this.settings.userAgentString = "${USER_AGENT_PREFIX}${BuildConfig.VERSION_NAME} ${settings.userAgentString}"
    }

    @SuppressLint("JavascriptInterface")
    fun addJsInterface(jsInterface: Any) {
        post {
            addJavascriptInterface(jsInterface, WEB_INTERFACE_NAME)
        }
    }

    fun removeJsInterface() {
        post {
            removeJavascriptInterface(WEB_INTERFACE_NAME)
        }
    }

    fun sendJsMessage(message: String) {
        post {
            postWebMessage(WebMessage(message), "*".toUri())
        }
    }

    fun destroyWebView() {
        webViewClient = WebViewClient()
        webChromeClient = null
        stopLoading()
        removeJsInterface()
        destroy()
    }

    private companion object {
        const val USER_AGENT_PREFIX: String = "pera_android_"
        const val WEB_INTERFACE_NAME: String = "peraMobileInterface"
    }
}
