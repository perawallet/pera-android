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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.perapackagemanager.ui.PeraPackageManager
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.ON_BACK_PRESSED
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceEventResponseMapper
import com.algorand.android.ui.webview.bridge.mapper.SettingsWebResponseMapper
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.CanOpenUri
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.CloseWebView
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.GetAddresses
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.GetSettings
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.LogAnalyticsEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.NotifyUser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.OpenNativeUri
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.OpenSystemBrowser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType.PushPublicWebView
import com.algorand.android.ui.webview.bridge.usecase.GetGetAddressesWebResponse
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PeraWebViewViewModel @Inject constructor(
    private val getGetAddressesWebResponse: GetGetAddressesWebResponse,
    private val peraEventTracker: PeraEventTracker,
    private val packageManager: PeraPackageManager,
    private val responseMapper: PeraWebInterfaceEventResponseMapper,
    private val settingsResponseMapper: SettingsWebResponseMapper,
    private val getDeviceConfig: GetDeviceConfig,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

    fun processBackPress() {
        val message = responseMapper.invoke(ON_BACK_PRESSED, "")
        eventDelegate.sendEvent(viewModelScope, ViewEvent.SendWebMessage(message))
    }

    fun processWebEvent(event: PeraInternalWebInterfaceEvent) {
        viewModelScope.launch {
            when (event.type) {
                is LogAnalyticsEvent -> logAnalyticsEvent(event.name, event.type.payload)
                is NotifyUser -> eventDelegate.sendEvent(ViewEvent.NotifyUser(event.type))
                is OpenNativeUri -> eventDelegate.sendEvent(ViewEvent.OpenNativeUri(event.type.uri))
                is OpenSystemBrowser -> eventDelegate.sendEvent(ViewEvent.OpenSystemBrowser(event.type.url))
                is PushPublicWebView -> eventDelegate.sendEvent(ViewEvent.NavigateToPublicWebView(event.type))
                is CanOpenUri -> sendWebMessage(event.name, packageManager.canOpenUri(event.type.uri))
                CloseWebView -> eventDelegate.sendEvent(ViewEvent.NavigateBack)
                GetAddresses -> sendWebMessage(event.name, getGetAddressesWebResponse())
                GetSettings -> sendWebMessage(event.name, settingsResponseMapper.mapInternalResponse(getDeviceConfig()))
            }
        }
    }

    private suspend fun logAnalyticsEvent(eventName: String, payload: Map<String, String>?) {
        if (payload == null) {
            peraEventTracker.logEvent(eventName)
        } else {
            peraEventTracker.logEvent(eventName, payload)
        }
    }

    private suspend fun sendWebMessage(eventName: String, response: Any) {
        val responseMessage = responseMapper(eventName, response)
        eventDelegate.sendEvent(ViewEvent.SendWebMessage(responseMessage))
    }

    sealed interface ViewEvent {
        data class SendWebMessage(val message: String) : ViewEvent
        data class NavigateToPublicWebView(val params: PushPublicWebView) : ViewEvent
        data class OpenSystemBrowser(val url: String) : ViewEvent
        data class OpenNativeUri(val uri: String) : ViewEvent
        data class NotifyUser(val params: PeraInternalWebInterfaceEvent.EventType.NotifyUser) : ViewEvent
        data object NavigateBack : ViewEvent
    }
}
