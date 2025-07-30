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

package com.algorand.android.ui.register.initialregisterintro

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.initialregisterintro.NewOnboardingCreateNewAccountEventTracker
import com.algorand.android.modules.tracking.onboarding.register.initialregisterintro.NewOnboardingImportAccountEventTracker
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialRegisterIntroViewModel @Inject constructor(
    private val bip39WalletProvider: Bip39WalletProvider,
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper,
    private val newOnboardingCreateNewAccountEventTracker: NewOnboardingCreateNewAccountEventTracker,
    private val newOnboardingImportAccountEventTracker: NewOnboardingImportAccountEventTracker,
) : BaseViewModel() {

    fun createHdKeyAccount(): AccountCreation {
        val wallet = bip39WalletProvider.createBip39Wallet()
        val hdKeyAddress = wallet.generateAddress(HdKeyAddressIndex())
        val hdKeyType = accountCreationHdKeyTypeMapper(wallet.getEntropy().value, hdKeyAddress, null)
        return AccountCreation(
            address = hdKeyAddress.address,
            customName = null,
            type = hdKeyType,
            creationType = CreationType.CREATE
        ).also {
            wallet.invalidate()
        }
    }

    fun logNewOnboardingCreateNewAccountClickEvent() {
        viewModelScope.launch {
            newOnboardingCreateNewAccountEventTracker.logNewOnboardingCreateNewAccountEvent()
        }
    }

    fun logNewOnboardingImportClickEvent() {
        viewModelScope.launch {
            newOnboardingImportAccountEventTracker.logNewOnboardingImportAccountEvent()
        }
    }
}
