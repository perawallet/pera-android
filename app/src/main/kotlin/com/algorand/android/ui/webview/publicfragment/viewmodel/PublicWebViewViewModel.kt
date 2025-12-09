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

package com.algorand.android.ui.webview.publicfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceEventResponseMapper
import com.algorand.android.ui.webview.bridge.mapper.SettingsWebResponseMapper
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent.EventType.GetPublicSettings
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewEvent
import com.algorand.wallet.foundation.json.rpc.JsonRpcResponse
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PublicWebViewViewModel @Inject constructor(
    private val settingsResponseMapper: SettingsWebResponseMapper,
    private val responseMapper: PeraWebInterfaceEventResponseMapper,
    private val getDeviceConfig: GetDeviceConfig,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

    fun processWebEvent(eventResult: List<PeraWebInterfaceEventResult>) {
        viewModelScope.launch {
            val eventResponses = mutableListOf<JsonRpcResponse>()
            eventResult.forEach { event ->
                when (event.result) {
                    is PeraWebInterfaceEventResult.Result.Error -> {
                        if (event.id != null) {
                            val response = responseMapper.mapErrorResponse(event.id, event.result.code)
                            eventResponses.add(response)
                        }
                    }
                    is PeraWebInterfaceEventResult.Result.Success -> {
                        if (event.result.event !is PeraPublicWebInterfaceEvent) return@forEach
                        when (event.result.event.eventType) {
                            is GetPublicSettings -> {
                                if (event.id != null) {
                                    val publicSettings = settingsResponseMapper.mapPublicResponse(getDeviceConfig())
                                    val response = responseMapper.mapSuccessResponse(event.id, publicSettings)
                                    eventResponses.add(response)
                                }
                            }
                        }
                    }
                }
            }
            if (eventResponses.isNotEmpty()) {
                val message = responseMapper.mapResponseMessage(eventResponses)
                eventDelegate.sendEvent(ViewEvent.SendWebMessage(message))
            }
        }
    }

    sealed interface ViewEvent {
        data class SendWebMessage(val message: String) : ViewEvent
    }
}
