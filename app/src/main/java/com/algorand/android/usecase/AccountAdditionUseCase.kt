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

import com.algorand.android.core.BaseUseCase
import com.algorand.android.models.AccountCreation
import com.algorand.android.utils.analytics.logRegisterEvent
import com.algorand.common.account.core.domain.model.CreateAccount
import com.algorand.common.account.core.domain.model.CreateAccount.Type
import com.algorand.common.account.core.domain.usecase.AddAlgo25Account
import com.algorand.common.account.core.domain.usecase.AddLedgerBleAccount
import com.algorand.common.account.core.domain.usecase.AddNoAuthAccount
import com.algorand.common.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.common.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AccountAdditionUseCase @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val registrationUseCase: RegistrationUseCase,
    private val addAlgo25Account: AddAlgo25Account,
    private val addLedgerBleAccount: AddLedgerBleAccount,
    private val addNoAuthAccount: AddNoAuthAccount,
    private val updateNoAuthAccountToAlgo25: UpdateNoAuthAccountToAlgo25,
    private val updateNoAuthAccountToLedgerBle: UpdateNoAuthAccountToLedgerBle
) : BaseUseCase() {

    suspend fun addNewAccount(accountCreation: AccountCreation) {
        firebaseAnalytics.logRegisterEvent(accountCreation.creationType)
        addAccount(accountCreation.toCreateAccount())
        if (!registrationUseCase.getRegistrationSkipped()) {
            registrationUseCase.setRegistrationSkipPreferenceAsSkipped()
        }
    }

    suspend fun updateTypeOfWatchAccount(accountCreation: CreateAccount) {
        val address = accountCreation.address
        with(accountCreation.type) {
            when (this) {
                is Type.Algo25 -> updateNoAuthAccountToAlgo25(address, secretKey)
                is Type.LedgerBle -> updateNoAuthAccountToLedgerBle(
                    address,
                    deviceMacAddress,
                    bluetoothName.orEmpty(),
                    indexInLedger
                )
                is Type.NoAuth -> Unit
            }
        }
    }

    private suspend fun addAccount(createAccount: CreateAccount) {
        when (createAccount.type) {
            is Type.Algo25 -> createAlgo25Account(createAccount, createAccount.type as Type.Algo25)
            is Type.LedgerBle -> createLedgerBleAccount(createAccount, createAccount.type as Type.LedgerBle)
            is Type.NoAuth -> createNoAuthAccount(createAccount)
        }
    }

    private suspend fun createAlgo25Account(createAccount: CreateAccount, type: Type.Algo25) {
        with(createAccount) {
            addAlgo25Account(address, type.secretKey, isBackedUp, customName)
        }
    }

    private suspend fun createLedgerBleAccount(createAccount: CreateAccount, type: Type.LedgerBle) {
        with(createAccount) {
            addLedgerBleAccount(address, type.deviceMacAddress, type.indexInLedger, customName, type.bluetoothName)
        }
    }

    private suspend fun createNoAuthAccount(createAccount: CreateAccount) {
        addNoAuthAccount(createAccount.address, createAccount.customName)
    }
}
