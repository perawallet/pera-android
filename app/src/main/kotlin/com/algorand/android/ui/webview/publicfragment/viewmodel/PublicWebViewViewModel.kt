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
import com.algorand.android.ui.webview.bridge.PeraWebViewJsBridge
import com.algorand.android.ui.webview.bridge.PeraWebViewPublicJsBridge
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceEventResponseMapper
import com.algorand.android.ui.webview.bridge.mapper.SettingsWebResponseMapper
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent.EventType.GetPublicSettings
import com.algorand.android.ui.webview.publicfragment.model.PublicWebViewFragmentNavArgs
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewEvent
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState.Content.FavoriteState
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PublicWebViewViewModel @Inject constructor(
    private val settingsResponseMapper: SettingsWebResponseMapper,
    private val responseMapper: PeraWebInterfaceEventResponseMapper,
    private val getDeviceConfig: GetDeviceConfig,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState(args: PublicWebViewFragmentNavArgs) {
        stateDelegate.onState<ViewState.Idle> {
            val jsInterface = PeraWebViewPublicJsBridge(::processWebEvent)
            stateDelegate.updateState { ViewState.Content(args.url, jsInterface, getFavoriteState(args.isFavorite)) }
        }
    }

    private fun getFavoriteState(isFavorite: Boolean?): FavoriteState {
        return when (isFavorite) {
            true -> FavoriteState.Favorite
            false -> FavoriteState.Unfavorite
            null -> FavoriteState.Hidden
        }
    }

    private fun processWebEvent(event: PeraPublicWebInterfaceEvent) {
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

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val url: String,
            val jsInterface: PeraWebViewJsBridge,
            val favoriteState: FavoriteState
        ) : ViewState {
            sealed interface FavoriteState {
                data object Hidden : FavoriteState
                data object Favorite : FavoriteState
                data object Unfavorite : FavoriteState
            }
        }
    }

    sealed interface ViewEvent {
        data class SendWebMessage(val message: String) : ViewEvent
    }
}
