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
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.CloseWebView
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.LogAnalyticsEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.OpenNativeUri
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.OpenSystemBrowser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.PushPublicWebView
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.CanOpenUri
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.GetAddresses
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.GetPublicSettings
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.GetSettings
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult.Result
import com.algorand.android.ui.webview.bridge.usecase.GetGetAddressesWebResponse
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel.ViewEvent
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.foundation.json.rpc.JsonRpcResponse
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
        val request = responseMapper.mapRequest(ON_BACK_PRESSED, null)
        val message = responseMapper.mapResponseMessage(listOf(request))
        eventDelegate.sendEvent(viewModelScope, ViewEvent.SendWebMessage(message))
    }

    fun processWebEvents(eventResult: List<PeraWebInterfaceEventResult>) {
        viewModelScope.launch {
            val eventResponses = mutableListOf<JsonRpcResponse>()
            eventResult.forEach { event ->
                when (event.result) {
                    is Result.Error -> {
                        if (event.id != null) {
                            val response = responseMapper.mapErrorResponse(event.id, event.result.code)
                            eventResponses.add(response)
                        }
                    }
                    is Result.Success -> {
                        when (event.result.event) {
                            is Command -> processCommandEvent(event.result.event)
                            is Query -> {
                                if (event.id != null) {
                                    val response = getQueryEventResponse(event.result.event)
                                    eventResponses.add(responseMapper.mapSuccessResponse(event.id, response))
                                }
                            }
                        }
                    }
                }
            }
            if (eventResponses.isNotEmpty()) {
                sendWebMessage(eventResponses)
            }
        }
    }

    private suspend fun processCommandEvent(event: PeraInternalWebInterfaceEvent) {
        when (event) {
            CloseWebView -> eventDelegate.sendEvent(ViewEvent.NavigateBack)
            is LogAnalyticsEvent -> logAnalyticsEvent(event)
            is NotifyUser -> eventDelegate.sendEvent(ViewEvent.NotifyUser(event))
            is OpenNativeUri -> eventDelegate.sendEvent(ViewEvent.OpenNativeUri(event.uri))
            is OpenSystemBrowser -> eventDelegate.sendEvent(ViewEvent.OpenSystemBrowser(event.url))
            is PushPublicWebView -> eventDelegate.sendEvent(ViewEvent.NavigateToPublicWebView(event))
            else -> Unit
        }
    }

    private suspend fun getQueryEventResponse(event: Query): Any {
        return when (event.type) {
            is CanOpenUri -> packageManager.canOpenUri(event.type.uri)
            GetAddresses -> getGetAddressesWebResponse()
            GetSettings -> settingsResponseMapper.mapInternalResponse(getDeviceConfig())
            GetPublicSettings -> settingsResponseMapper.mapPublicResponse(getDeviceConfig())
        }
    }

    private suspend fun logAnalyticsEvent(command: LogAnalyticsEvent) {
        with(command) {
            if (payload == null) {
                peraEventTracker.logEvent(name)
            } else {
                peraEventTracker.logEvent(name, payload)
            }
        }
    }

    private suspend fun sendWebMessage(response: List<Any>) {
        val message = responseMapper.mapResponseMessage(response)
        eventDelegate.sendEvent(ViewEvent.SendWebMessage(message))
    }

    sealed interface ViewEvent {
        data class SendWebMessage(val message: String) : ViewEvent
        data class NavigateToPublicWebView(val params: PushPublicWebView) : ViewEvent
        data class OpenSystemBrowser(val url: String) : ViewEvent
        data class OpenNativeUri(val uri: String) : ViewEvent
        data class NotifyUser(val params: Command.NotifyUser) : ViewEvent
        data object NavigateBack : ViewEvent
    }
}
