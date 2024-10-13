package com.algorand.android.modules.card

import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.card.model.CardsPreview
import com.algorand.android.modules.perawebview.GetAuthorizedAddressesWebMessage
import com.algorand.android.modules.perawebview.GetDeviceIdWebMessage
import com.algorand.android.modules.perawebview.ParseOpenSystemBrowserUrl
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewViewModel
import com.algorand.android.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val getAuthorizedAddressesWebMessage: GetAuthorizedAddressesWebMessage,
    private val getDeviceIdWebMessage: GetDeviceIdWebMessage,
    private val parseOpenSystemBrowserUrl: ParseOpenSystemBrowserUrl
) : BasePeraWebViewViewModel() {

    private val _cardsPreviewFlow = MutableStateFlow<CardsPreview>(CardsPreview())
    val cardsPreviewFlow: StateFlow<CardsPreview?>
        get() = _cardsPreviewFlow.asStateFlow()

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

    fun getOpenSystemBrowserUrl(jsonPayload: String): String? {
        return parseOpenSystemBrowserUrl(jsonPayload)
    }
}
