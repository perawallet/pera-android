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

package com.algorand.android.modules.settings.domain.usecase

import com.algorand.android.encryption.domain.usecase.AndroidEncryptionManager
import com.algorand.android.models.Account
import com.algorand.android.models.AccountCreation
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.usecase.GetLocalAccountsFromSharedPrefUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

class MigrateTo6xUseCase @Inject constructor(
    private val getLocalAccountsFromSharedPrefUseCase: GetLocalAccountsFromSharedPrefUseCase,
    private val androidEncryptionManager: AndroidEncryptionManager,
    private val aesPlatformManager: AESPlatformManager,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val peraExceptionLogger: PeraExceptionLogger
) {

    suspend fun invoke(): PeraResult<Int> {
        return try {
            androidEncryptionManager.initializeEncryptionManager()

            val localAccounts = getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
            var migratedCount = 0
            localAccounts?.forEach { localAccount ->
                val migrateAccount = createMigrationAccount(localAccount)
                migrateAccount?.let {
                    accountAdditionUseCase.addNewAccount(it)
                    migratedCount++
                }
            }

            PeraResult.Success(migratedCount)
        } catch (e: Exception) {
            peraExceptionLogger.logException(e)
            PeraResult.Error(e)
        }
    }

    private fun createMigrationAccount(localAccount: Account): AccountCreation? {
        return when (localAccount.detail) {
            is Account.Detail.Standard,
            is Account.Detail.Rekeyed,
            is Account.Detail.RekeyedAuth -> createStandardAccount(localAccount)

            is Account.Detail.Ledger -> createLedgerAccount(localAccount)

            is Account.Detail.Watch -> createWatchAccount(localAccount)

            else -> null
        }
    }

    private fun createStandardAccount(localAccount: Account): AccountCreation {
        return localAccount.getSecretKey()?.let {
            AccountCreation(
                address = localAccount.address,
                customName = localAccount.name,
                orderIndex = localAccount.index,
                isBackedUp = localAccount.isBackedUp,
                type = AccountCreation.Type.Algo25(
                    aesPlatformManager.encryptByteArray(it)
                ),
                creationType = CreationType.RECOVER
            )
        } ?: createWatchAccount(localAccount)
    }

    private fun createLedgerAccount(localAccount: Account): AccountCreation {
        val ledgerDetail = localAccount.detail as Account.Detail.Ledger
        return AccountCreation(
            address = localAccount.address,
            customName = localAccount.name,
            orderIndex = localAccount.index,
            isBackedUp = localAccount.isBackedUp,
            type = AccountCreation.Type.LedgerBle(
                deviceMacAddress = ledgerDetail.bluetoothAddress,
                indexInLedger = ledgerDetail.positionInLedger,
                bluetoothName = ledgerDetail.bluetoothName
            ),
            creationType = CreationType.LEDGER
        )
    }

    private fun createWatchAccount(localAccount: Account): AccountCreation {
        return AccountCreation(
            address = localAccount.address,
            customName = localAccount.name,
            orderIndex = localAccount.index,
            isBackedUp = localAccount.isBackedUp,
            type = AccountCreation.Type.NoAuth,
            creationType = CreationType.WATCH
        )
    }
}
