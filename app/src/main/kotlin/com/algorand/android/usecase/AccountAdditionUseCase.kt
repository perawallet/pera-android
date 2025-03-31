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
import com.algorand.wallet.account.core.domain.model.CreateAccount
import com.algorand.wallet.account.core.domain.model.CreateAccount.Type
import com.algorand.wallet.account.core.domain.usecase.AddAlgo25Account
import com.algorand.wallet.account.core.domain.usecase.AddHdKeyAccount
import com.algorand.wallet.account.core.domain.usecase.AddHdSeed
import com.algorand.wallet.account.core.domain.usecase.AddLedgerBleAccount
import com.algorand.wallet.account.core.domain.usecase.AddNoAuthAccount
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToHdKey
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

@Suppress("LongParameterList")
class AccountAdditionUseCase @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val registrationUseCase: RegistrationUseCase,
    private val addHdKeyAccount: AddHdKeyAccount,
    private val addHdSeed: AddHdSeed,
    private val addAlgo25Account: AddAlgo25Account,
    private val addLedgerBleAccount: AddLedgerBleAccount,
    private val addNoAuthAccount: AddNoAuthAccount,
    private val updateNoAuthAccountToAlgo25: UpdateNoAuthAccountToAlgo25,
    private val updateNoAuthAccountToHdKey: UpdateNoAuthAccountToHdKey,
    private val updateNoAuthAccountToLedgerBle: UpdateNoAuthAccountToLedgerBle,
    private val aesPlatformManager: AESPlatformManager
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
                is Type.HdKey -> {
                    aesPlatformManager.decryptByteArray(this.encryptedEntropy).let { entropy ->
                        addHdSeed(entropy).getDataOrNull()?.let { seedId ->
                            updateNoAuthAccountToHdKey(
                                address,
                                publicKey,
                                encryptedPrivateKey,
                                seedId,
                                account,
                                change,
                                keyIndex,
                                derivationType,
                            )
                        }
                    }
                }
                is Type.Algo25 -> {
                    val secretKey = aesPlatformManager.decryptByteArray(encryptedSecretKey)
                    updateNoAuthAccountToAlgo25(
                        address, secretKey
                    )
                }
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
            is Type.HdKey -> createHdKeyAccount(createAccount, createAccount.type as Type.HdKey)
            is Type.Algo25 -> createAlgo25Account(createAccount, createAccount.type as Type.Algo25)
            is Type.LedgerBle -> createLedgerBleAccount(createAccount, createAccount.type as Type.LedgerBle)
            is Type.NoAuth -> createNoAuthAccount(createAccount)
        }
    }

    private suspend fun createHdKeyAccount(createAccount: CreateAccount, type: Type.HdKey) {
        with(createAccount) {
            aesPlatformManager.decryptByteArray(type.encryptedPrivateKey).let { privateKey ->
                aesPlatformManager.decryptByteArray(type.encryptedEntropy).let { entropy ->
                    val seedIdResult = addHdSeed(entropy)
                    val seedId = seedIdResult.getDataOrNull()
                    if (seedIdResult.isSuccess && seedId != null) {
                        addHdKeyAccount(
                            address,
                            type.publicKey,
                            privateKey,
                            seedId,
                            type.account,
                            type.change,
                            type.keyIndex,
                            type.derivationType,
                            isBackedUp,
                            customName,
                            createAccount.orderIndex
                        )
                    }
                }
            }
        }
    }

    private suspend fun createAlgo25Account(createAccount: CreateAccount, type: Type.Algo25) {
        with(createAccount) {
            var secretKey = aesPlatformManager.decryptByteArray(type.encryptedSecretKey)
            addAlgo25Account(
                address,
                secretKey.copyOf(),
                isBackedUp,
                customName,
                createAccount.orderIndex
            )
            secretKey.clearFromMemory()
        }
    }

    private suspend fun createLedgerBleAccount(createAccount: CreateAccount, type: Type.LedgerBle) {
        with(createAccount) {
            addLedgerBleAccount(
                address,
                type.deviceMacAddress,
                type.indexInLedger,
                customName,
                type.bluetoothName,
                createAccount.orderIndex
            )
        }
    }

    private suspend fun createNoAuthAccount(createAccount: CreateAccount) {
        addNoAuthAccount(
            createAccount.address,
            createAccount.customName,
            createAccount.orderIndex
        )
    }
}
