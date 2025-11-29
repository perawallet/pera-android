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
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewEvent
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

    fun processWebEvent(event: PeraPublicWebInterfaceEvent) {
        viewModelScope.launch {
            when (event.eventType) {
                GetPublicSettings -> {
                    val publicSettings = settingsResponseMapper.mapPublicResponse(getDeviceConfig())
                    val response = responseMapper(event.name, publicSettings)
                    eventDelegate.sendEvent(ViewEvent.SendWebMessage(response))
                }
            }
        }
    }

    sealed interface ViewEvent {
        data class SendWebMessage(val message: String) : ViewEvent
    }
}
