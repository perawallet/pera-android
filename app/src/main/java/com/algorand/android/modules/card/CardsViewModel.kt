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

package com.algorand.android.modules.card

import androidx.lifecycle.viewModelScope
import com.algorand.android.BuildConfig.CARDS_MAINNET_URL
import com.algorand.android.BuildConfig.CARDS_TESTNET_URL
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.modules.card.model.CardsPreview
import com.algorand.android.modules.currency.domain.usecase.CurrencyUseCase
import com.algorand.android.modules.perawebview.GetAuthorizedAddressesWebMessage
import com.algorand.android.modules.perawebview.GetDeviceIdWebMessage
import com.algorand.android.modules.perawebview.ParseOpenSystemBrowserUrl
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewViewModel
import com.algorand.android.usecase.GetIsActiveNodeTestnetUseCase
import com.algorand.android.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val getIsActiveNodeTestnetUseCase: GetIsActiveNodeTestnetUseCase,
    private val getAuthorizedAddressesWebMessage: GetAuthorizedAddressesWebMessage,
    private val getDeviceIdWebMessage: GetDeviceIdWebMessage,
    private val parseOpenSystemBrowserUrl: ParseOpenSystemBrowserUrl,
    private val currencyUseCase: CurrencyUseCase
) : BasePeraWebViewViewModel() {

    private val _cardsPreviewFlow = MutableStateFlow<CardsPreview>(CardsPreview())
    val cardsPreviewFlow: StateFlow<CardsPreview>
        get() = _cardsPreviewFlow.asStateFlow()

    override fun onPageFinished(title: String?, url: String?) {
        super.onPageFinished(title, url)
        _cardsPreviewFlow.value = cardsPreviewFlow.value.copy(onPageFinished = Event(Unit))
    }

    fun getCardsUrl(): String {
        return if (isConnectedToTestnet())
            CARDS_TESTNET_URL
        else
            CARDS_MAINNET_URL
    }

    fun getAuthorizedAddresses() {
        viewModelScope.launch {
            val authAddressesMessage = getAuthorizedAddressesWebMessage()
            _cardsPreviewFlow.update {
                it.copy(sendMessageEvent = Event(authAddressesMessage))
            }
        }
    }

    fun getDeviceId() {
        viewModelScope.launch {
            val deviceIdMessage = getDeviceIdWebMessage() ?: return@launch
            _cardsPreviewFlow.update {
                it.copy(sendMessageEvent = Event(deviceIdMessage))
            }
        }
    }

    override fun onError() {
        viewModelScope.launch {
            _cardsPreviewFlow.update {
                it.copy(errorEvent = Event(WebViewError.NO_CONNECTION))
            }
        }
    }

    override fun onHttpError() {
        viewModelScope.launch {
            _cardsPreviewFlow.update {
                it.copy(errorEvent = Event(WebViewError.HTTP_ERROR))
            }
        }
    }

    fun getOpenSystemBrowserUrl(jsonPayload: String): String? {
        return parseOpenSystemBrowserUrl(jsonPayload)
    }

    fun getPrimaryCurrencyId(): String {
        return currencyUseCase.getPrimaryCurrencyId()
    }

    fun isConnectedToTestnet(): Boolean {
        return getIsActiveNodeTestnetUseCase.invoke()
    }
}
