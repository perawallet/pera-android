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

package com.algorand.android.ui.common.warningconfirmation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.OnboardingPassphraseUnderstandEventTracker
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.getOrElse
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupInfoViewModel @Inject constructor(
    private val onboardingPassphraseUnderstandEventTracker: OnboardingPassphraseUnderstandEventTracker,
    private val algoAccountSdk: AlgoAccountSdk,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val accountType: AccountType = savedStateHandle.getOrElse(
        ACCOUNT_TYPE,
        AccountType.Algo25
    )

    fun logOnboardingIUnderstandClickEvent() {
        viewModelScope.launch {
            onboardingPassphraseUnderstandEventTracker.logOnboardingPassphraseUnderstandEvent()
        }
    }

    fun createAccount(): AccountCreation {
        if (accountType == AccountType.HdKey) {
            val account = algoAccountSdk.createHdAccount()
            return AccountCreation(
                address = account.address,
                customName = null,
                isBackedUp = false,
                type = AccountCreation.Type.HdKey(
                    account.publicKey,
                    account.encryptedPrivateKey,
                    0,
                    account.account,
                    account.change,
                    account.keyIndex,
                    account.derivationType,
                ),
                creationType = CreationType.CREATE
            )
        } else {
            val account = algoAccountSdk.createAlgo25Account()
            return AccountCreation(
                address = account.address,
                customName = null,
                isBackedUp = false,
                type = AccountCreation.Type.Algo25(account.encryptedSecretKey),
                creationType = CreationType.CREATE
            )
        }
    }

    companion object {
        private const val ACCOUNT_TYPE = "accountType"
    }
}
