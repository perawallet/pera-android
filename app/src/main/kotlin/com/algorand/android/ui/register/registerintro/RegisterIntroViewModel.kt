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

package com.algorand.android.ui.register.registerintro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.RegisterIntroPreview
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.android.usecase.IsOnHdWalletUseCase
import com.algorand.android.usecase.RegisterIntroPreviewUseCase
import com.algorand.android.usecase.RegistrationUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.getOrElse
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterIntroViewModel @Inject constructor(
    private val registerIntroPreviewUseCase: RegisterIntroPreviewUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val isOnHdWalletUseCase: IsOnHdWalletUseCase,
    private val algoAccountSdk: AlgoAccountSdk,
    private val aesPlatformManager: AESPlatformManager,
    private val bip39WalletProvider: Bip39WalletProvider,
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val isShowingCloseButton = savedStateHandle.getOrElse(IS_SHOWING_CLOSE_BUTTON_KEY, false)

    private val _registerIntroPreviewFlow = MutableStateFlow<RegisterIntroPreview?>(null)
    val registerIntroPreviewFlow: StateFlow<RegisterIntroPreview?> = _registerIntroPreviewFlow

    init {
        getRegisterIntroPreview()
    }

    fun setRegisterSkip() {
        registrationUseCase.setRegistrationSkipPreferenceAsSkipped()
    }

    private fun getRegisterIntroPreview() {
        viewModelScope.launch {
            registerIntroPreviewUseCase.getRegisterIntroPreview(isShowingCloseButton).collectLatest {
                _registerIntroPreviewFlow.emit(it)
            }
        }
    }

    fun logOnboardingWelcomeAccountCreateClickEvent() {
        viewModelScope.launch {
            registerIntroPreviewUseCase.logOnboardingCreateNewAccountClickEvent()
        }
    }

    fun logOnboardingWelcomeAccountRecoverClickEvent() {
        viewModelScope.launch {
            registerIntroPreviewUseCase.logOnboardingWelcomeAccountRecoverClickEvent()
        }
    }

    fun isHdWalletToggleEnabled(): Boolean {
        return isOnHdWalletUseCase.invoke()
    }

    fun createHdKeyAccount(): AccountCreation {
        val wallet = bip39WalletProvider.createBip39Wallet()
        val hdKeyAddress = wallet.generateAddress(HdKeyAddressIndex())
        val hdKeyType = accountCreationHdKeyTypeMapper(wallet.getEntropy().value, hdKeyAddress, seedId = null)
        return AccountCreation(
            address = hdKeyAddress.address,
            customName = null,
            isBackedUp = false,
            type = hdKeyType,
            creationType = CreationType.CREATE
        )
    }

    fun createAlgo25Account(): AccountCreation? {
        val account = algoAccountSdk.createAlgo25Account() ?: return null
        return AccountCreation(
            address = account.address,
            customName = null,
            isBackedUp = false,
            type = AccountCreation.Type.Algo25(aesPlatformManager.encryptByteArray(account.secretKey)),
            creationType = CreationType.CREATE
        )
    }

    companion object {
        private const val IS_SHOWING_CLOSE_BUTTON_KEY = "isShowingCloseButton"
    }
}
