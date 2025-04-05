/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.register.nameregistration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.ui.NameRegistrationPreview
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.usecase.IsOnHdWalletUseCase
import com.algorand.android.usecase.NameRegistrationPreviewUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.local.domain.usecase.GetMaxHdSeedId
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NameRegistrationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nameRegistrationPreviewUseCase: NameRegistrationPreviewUseCase,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
    private val isOnHdWalletUseCase: IsOnHdWalletUseCase,
    private val getMaxHdSeedId: GetMaxHdSeedId,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), EventViewModel<NameRegistrationViewModel.ViewEvent> by eventDelegate {

    private val _nameRegistrationPreviewFlow = MutableStateFlow(getInitialPreview())
    val nameRegistrationPreviewFlow: Flow<NameRegistrationPreview>
        get() = _nameRegistrationPreviewFlow

    private val accountCreation = savedStateHandle.get<AccountCreation>(ACCOUNT_CREATION_KEY)
    private val accountAddress = accountCreation?.address
    private val accountName = accountCreation?.customName
    private val accountType = accountCreation?.type
    private var walletId: Int? = null

    val predefinedAccountName: String
        get() = accountName.takeUnless { it.isNullOrBlank() } ?: accountAddress.toShortenedAddress()

    init {
        updatePreviewWithHdWalletData()
    }

    private fun updatePreviewWithHdWalletData() {
        viewModelScope.launch(Dispatchers.IO) {
            walletId = (getMaxHdSeedId.invoke() ?: 0) + 1
            walletId?.let {
                nameRegistrationPreviewUseCase.getInitialPreviewWithHdWalletData(
                    walletId = it
                ).let {
                    _nameRegistrationPreviewFlow.emit(it)
                }
            }
        }
    }

    private fun updatePreviewWithAccountCreation(accountCreation: AccountCreation?, inputName: String) {
        viewModelScope.launch {
            nameRegistrationPreviewUseCase.getPreviewWithAccountCreation(
                accountCreation = accountCreation,
                inputName = inputName,
                walletId = walletId
            )?.let {
                _nameRegistrationPreviewFlow.emit(it)
            }
        }
    }

    fun updateWatchAccount(accountCreation: AccountCreation) {
        viewModelScope.launch {
            nameRegistrationPreviewUseCase.updateTypeOfWatchAccount(accountCreation)
            nameRegistrationPreviewUseCase.updateNameOfWatchAccount(accountCreation)
            _nameRegistrationPreviewFlow.emit(nameRegistrationPreviewUseCase.getOnWatchAccountUpdatedPreview())
        }
    }

    fun addNewAccount(account: AccountCreation) {
        viewModelScope.launchIO {
            nameRegistrationPreviewUseCase.addNewAccount(account)
            _nameRegistrationPreviewFlow.update {
                it.copy(handleNextNavigationEvent = Event(Unit))
            }
        }
    }

    private fun getInitialPreview(): NameRegistrationPreview {
        return nameRegistrationPreviewUseCase.getInitialPreview()
    }

    fun isOnHdWallet(): Boolean {
        return isOnHdWalletUseCase.invoke()
    }

    fun isHdKey(): Boolean {
        return accountType is AccountCreation.Type.HdKey
    }

    fun onNextButtonClick(inputName: String) {
        viewModelScope.launchIO {
            if (isAccountLimitExceedUseCase.isAccountLimitExceed()) {
                eventDelegate.sendEvent(ViewEvent.ShowMaxAccountLimitExceededError)
            } else {
                updatePreviewWithAccountCreation(accountCreation, inputName)
            }
        }
    }

    companion object {
        private const val ACCOUNT_CREATION_KEY = "accountCreation"
    }

    sealed interface ViewEvent {
        data object ShowMaxAccountLimitExceededError : ViewEvent
    }
}
