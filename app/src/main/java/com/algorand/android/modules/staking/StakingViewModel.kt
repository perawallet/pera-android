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

package com.algorand.android.modules.staking

import androidx.lifecycle.viewModelScope
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.modules.perawebview.GetAuthorizedAddressesWebMessage
import com.algorand.android.modules.perawebview.GetDeviceIdWebMessage
import com.algorand.android.modules.perawebview.ParseOpenSystemBrowserUrl
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewViewModel
import com.algorand.android.modules.staking.model.StakingPreview
import com.algorand.android.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StakingViewModel @Inject constructor(
    private val getAuthorizedAddressesWebMessage: GetAuthorizedAddressesWebMessage,
    private val getDeviceIdWebMessage: GetDeviceIdWebMessage,
    private val parseOpenSystemBrowserUrl: ParseOpenSystemBrowserUrl
) : BasePeraWebViewViewModel() {

    private val _stakingPreviewFlow = MutableStateFlow<StakingPreview>(StakingPreview())
    val stakingPreviewFlow: StateFlow<StakingPreview?>
        get() = _stakingPreviewFlow.asStateFlow()

    fun getAuthorizedAddresses() {
        viewModelScope.launch {
            val authAddressesMessage = getAuthorizedAddressesWebMessage()
            _stakingPreviewFlow.update {
                it.copy(sendMessageEvent = Event(authAddressesMessage))
            }
        }
    }

    fun getDeviceId() {
        viewModelScope.launch {
            val deviceIdMessage = getDeviceIdWebMessage() ?: return@launch
            _stakingPreviewFlow.update {
                it.copy(sendMessageEvent = Event(deviceIdMessage))
            }
        }
    }

    override fun onError() {
        viewModelScope.launch {
            _stakingPreviewFlow.update {
                it.copy(errorEvent = Event(WebViewError.NO_CONNECTION))
            }
        }
    }

    override fun onHttpError() {
        viewModelScope.launch {
            _stakingPreviewFlow.update {
                it.copy(errorEvent = Event(WebViewError.HTTP_ERROR))
            }
        }
    }

    fun getOpenSystemBrowserUrl(jsonPayload: String): String? {
        return parseOpenSystemBrowserUrl(jsonPayload)
    }
}
