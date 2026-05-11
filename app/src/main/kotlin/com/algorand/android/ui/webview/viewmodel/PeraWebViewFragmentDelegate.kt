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

package com.algorand.android.ui.webview.viewmodel

import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.algorand.android.core.BaseFragment
import com.algorand.android.discover.common.ui.model.PeraWebChromeClient
import com.algorand.android.discover.common.ui.model.PeraWebViewClient
import com.algorand.android.ui.vibration.PeraVibration
import com.algorand.android.ui.webview.bridge.PeraWebViewJsBridge
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Haptic
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Haptic.HapticType
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Message
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Sound
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.PushPublicWebView
import com.algorand.android.ui.webview.view.PeraMobileWebView
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent.NavigateBack
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent.NavigateToPublicWebView
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent.NotifyUser
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent.OpenNativeUri
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent.OpenSystemBrowser
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent.SendWebMessage
import com.algorand.android.utils.browser.openExternalBrowserApp

class PeraWebViewFragmentDelegate(
    private val fragment: BaseFragment,
    private val webView: PeraMobileWebView,
    private val listener: Listener,
    private val webViewClientListener: PeraWebViewClient.PeraWebViewClientListener
) {

    fun initWebView(url: String, jsInterface: PeraWebViewJsBridge) {
        webView.apply {
            webViewClient = PeraWebViewClient(webViewClientListener)
            webChromeClient = PeraWebChromeClient(webViewClientListener)
            addJsInterface(jsInterface)
            loadUrl(url)
        }
    }

    fun collectViewEvent(event: PeraWebViewViewModel.ViewEvent) {
        when (event) {
            NavigateBack -> listener.onNavigateBack()
            is NavigateToPublicWebView -> listener.onNavPublicWebView(event.params)
            is NotifyUser -> notifyUser(event)
            is OpenNativeUri -> openNativeUri(event.uri)
            is OpenSystemBrowser -> fragment.context?.openExternalBrowserApp(event.url)
            is SendWebMessage -> webView.sendJsMessage(event.message)
        }
    }

    private fun openNativeUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri.toUri()
        }
        if (intent.resolveActivity(fragment.context?.packageManager ?: return) != null) {
            fragment.startActivity(intent)
        }
    }

    private fun notifyUser(event: NotifyUser) {
        when (event.params) {
            is Haptic -> notifyHaptic(event.params)
            is Message -> notifyMessage(event.params)
            is Sound -> Unit // No implementation for sound yet
        }
    }

    private fun notifyHaptic(haptic: Haptic) {
        when (haptic.type) {
            HapticType.LIGHT -> PeraVibration.light()
            HapticType.MEDIUM -> PeraVibration.medium()
            HapticType.HEAVY -> PeraVibration.heavy()
            HapticType.SUCCESS -> PeraVibration.success()
            HapticType.WARNING -> PeraVibration.warning()
            HapticType.ERROR -> PeraVibration.error()
        }
    }

    private fun notifyMessage(message: Message) {
        when (message.type) {
            Message.MessageType.BANNER -> fragment.showAlertSuccess(message.message)
            Message.MessageType.TOAST -> {
                Toast.makeText(fragment.context ?: return, message.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface Listener {
        fun onNavPublicWebView(params: PushPublicWebView)
        fun onNavigateBack()
    }
}
