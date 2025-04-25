/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.qrscanning

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.deeplink.ui.DeeplinkHandler
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionDetail
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val deeplinkHandler: DeeplinkHandler,
    private val getAccountType: GetAccountType,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), EventViewModel<QrScannerViewModel.ViewEvent> by eventDelegate {

    private val _isQrCodeInProgressFlow = MutableSharedFlow<Boolean>()
    val isQrCodeInProgressFlow: SharedFlow<Boolean> = _isQrCodeInProgressFlow

    fun setQrCodeInProgress(isInProgress: Boolean) {
        viewModelScope.launch {
            _isQrCodeInProgressFlow.emit(isInProgress)
        }
    }

    fun setDeeplinkHandlerListener(listener: DeeplinkHandler.Listener) {
        deeplinkHandler.setListener(listener)
    }

    fun handleDeeplink(uri: String) {
        viewModelScope.launchIO {
            deeplinkHandler.handleDeepLink(uri)
        }
    }

    fun removeDeeplinkHandlerListener() {
        deeplinkHandler.setListener(null)
    }

    fun handleKeyRegDeepLink(deepLink: DeepLink.KeyReg) {
        val txnDetail = KeyRegTransactionDetail(
            address = deepLink.senderAddress,
            type = deepLink.type,
            voteKey = deepLink.voteKey,
            selectionPublicKey = deepLink.selkey,
            sprfkey = deepLink.sprfkey,
            voteFirstRound = deepLink.votefst,
            voteLastRound = deepLink.votelst,
            voteKeyDilution = deepLink.votekd,
            fee = deepLink.fee?.toBigIntegerOrNull(),
            note = deepLink.note,
            xnote = deepLink.xnote
        )

        viewModelScope.launchIO {
            val canSignTransaction = getAccountType(txnDetail.address)?.canSignTransaction() == true

            val viewEvent = if (canSignTransaction) {
                ViewEvent.NavigateToKeyRegTransactionFragment(txnDetail)
            } else {
                ViewEvent.ShowKeyRegDeeplinkError(txnDetail.address)
            }

            eventDelegate.sendEvent(viewEvent)
        }
    }

    interface ViewEvent {
        data class NavigateToKeyRegTransactionFragment(val transactionDetail: KeyRegTransactionDetail) : ViewEvent
        data class ShowKeyRegDeeplinkError(val address: String) : ViewEvent
    }
}
