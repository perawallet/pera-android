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

package com.algorand.android.usecase

import com.algorand.android.account.localaccount.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.android.account.localaccount.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.algorand.android.core.BaseUseCase
import com.algorand.android.core.component.domain.usecase.AddAccount
import com.algorand.android.models.CreateAccount
import com.algorand.android.utils.analytics.logRegisterEvent
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AccountAdditionUseCase @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val registrationUseCase: RegistrationUseCase,
    private val addAccount: AddAccount,
    private val updateNoAuthAccountToAlgo25: UpdateNoAuthAccountToAlgo25,
    private val updateNoAuthAccountToLedgerBle: UpdateNoAuthAccountToLedgerBle
) : BaseUseCase() {

    suspend fun addNewAccount(createAccount: CreateAccount) {
        firebaseAnalytics.logRegisterEvent(createAccount.creationType)
        addAccount(createAccount)
        if (!registrationUseCase.getRegistrationSkipped()) {
            registrationUseCase.setRegistrationSkipPreferenceAsSkipped()
        }
    }

    suspend fun updateTypeOfWatchAccount(accountCreation: CreateAccount) {
        with(accountCreation) {
            when (this) {
                is CreateAccount.Algo25 -> updateNoAuthAccountToAlgo25(address, secretKey)
                is CreateAccount.LedgerBle -> updateNoAuthAccountToLedgerBle(address, deviceMacAddress, indexInLedger)
                is CreateAccount.NoAuth -> Unit
            }
        }
    }

    private suspend fun addAccount(createAccount: CreateAccount) {
        when (createAccount) {
            is CreateAccount.Algo25 -> createAlgo25Account(createAccount)
            is CreateAccount.NoAuth -> createNoAuthAccount(createAccount)
            is CreateAccount.LedgerBle -> createLedgerBleAccount(createAccount)
        }
    }

    private suspend fun createAlgo25Account(createAccount: CreateAccount.Algo25) {
        with(createAccount) {
            addAccount.addAlgo25(address, secretKey, isBackedUp, customName)
        }
    }

    private suspend fun createLedgerBleAccount(createAccount: CreateAccount.LedgerBle) {
        with(createAccount) {
            addAccount.addLedgerBle(address, deviceMacAddress, indexInLedger, customName)
        }
    }

    private suspend fun createNoAuthAccount(createAccount: CreateAccount.NoAuth) {
        addAccount.addNoAuth(createAccount.address, createAccount.customName)
    }
}
