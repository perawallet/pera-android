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

package com.algorand.android.ui.accounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.User
import com.algorand.android.modules.transaction.domain.GetTransactionTargetUserDisplayName
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.getOrElse
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AccountsAddressScanActionViewModel @Inject constructor(
    private val getTransactionTargetUserDisplayName: GetTransactionTargetUserDisplayName,
    savedStateHandle: SavedStateHandle,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase
) : BaseViewModel(), EventViewModel<AccountsAddressScanActionViewModel.ViewEvent> by eventDelegate {

    private val accountAddress = savedStateHandle.getOrThrow<String>(ACCOUNT_ADDRESS_KEY)
    private val label: String? = savedStateHandle.getOrElse<String?>(LABEL_KEY, null)
    private var transactionTargetUserDisplayName: String = ""

    init {
        initTransactionTargetUserDisplayName()
    }

    fun getAccountAddress(): String = accountAddress

    fun getLabel(): String? = label

    fun getAssetTransactionArg(): AssetTransaction {
        return AssetTransaction(
            receiverUser = User(
                name = transactionTargetUserDisplayName,
                publicKey = accountAddress,
                imageUriAsString = null
            )
        )
    }

    fun onAddWatchAccountClick() {
        viewModelScope.launchIO {
            eventDelegate.sendEvent(
                if (isAccountLimitExceedUseCase.isAccountLimitExceed()) {
                    ViewEvent.ShowMaxAccountLimitExceededError
                } else {
                    ViewEvent.NavToRegisterWatchAccountNavigation(getAccountAddress())
                }
            )
        }
    }

    private fun initTransactionTargetUserDisplayName() {
        viewModelScope.launch {
            transactionTargetUserDisplayName = getTransactionTargetUserDisplayName(accountAddress)
        }
    }

    companion object {
        private const val ACCOUNT_ADDRESS_KEY = "accountAddress"
        private const val LABEL_KEY = "label"
    }

    sealed interface ViewEvent {
        data class NavToRegisterWatchAccountNavigation(val accountAddress: String) : ViewEvent
        data object ShowMaxAccountLimitExceededError : ViewEvent
    }
}
