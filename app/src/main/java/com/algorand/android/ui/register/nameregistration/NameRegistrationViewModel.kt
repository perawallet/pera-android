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

import androidx.lifecycle.*
import com.algorand.android.models.*
import com.algorand.android.models.ui.NameRegistrationPreview
import com.algorand.android.usecase.*
import com.algorand.android.utils.*
import com.algorand.android.utils.analytics.CreationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameRegistrationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nameRegistrationPreviewUseCase: NameRegistrationPreviewUseCase,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase
) : ViewModel() {

    private val _nameRegistrationPreviewFlow = MutableStateFlow(getInitialPreview())
    val nameRegistrationPreviewFlow: Flow<NameRegistrationPreview>
        get() = _nameRegistrationPreviewFlow

    private val accountCreation = savedStateHandle.getOrThrow<CreateAccount>(ACCOUNT_CREATION_KEY)
    private val accountAddress = accountCreation.address
    private val accountName = accountCreation.customName.orEmpty()

    val predefinedAccountName: String
        get() = accountName.takeIf { it.isNotBlank() } ?: accountAddress.toShortenedAddress()

    fun updatePreviewWithAccountCreation(accountCreation: CreateAccount, inputName: String) {
        viewModelScope.launch {
            nameRegistrationPreviewUseCase.getPreviewWithAccountCreation(
                accountCreation = accountCreation,
                inputName = inputName
            ).let {
                _nameRegistrationPreviewFlow.emit(it)
            }
        }
    }

    fun updateWatchAccount(accountCreation: CreateAccount) {
        viewModelScope.launch {
            nameRegistrationPreviewUseCase.updateTypeOfWatchAccount(accountCreation)
            nameRegistrationPreviewUseCase.updateNameOfWatchAccount(accountCreation)
            _nameRegistrationPreviewFlow.emit(nameRegistrationPreviewUseCase.getOnWatchAccountUpdatedPreview())
        }
    }

    fun addNewAccount(createAccount: CreateAccount) {
        viewModelScope.launchIO {
            nameRegistrationPreviewUseCase.addNewAccount(createAccount)
        }
    }

    private fun getInitialPreview(): NameRegistrationPreview {
        return nameRegistrationPreviewUseCase.getInitialPreview()
    }

    fun isAccountLimitExceed(): Boolean {
        return isAccountLimitExceedUseCase.isAccountLimitExceed()
    }

    companion object {
        private const val ACCOUNT_CREATION_KEY = "accountCreation"
    }
}
