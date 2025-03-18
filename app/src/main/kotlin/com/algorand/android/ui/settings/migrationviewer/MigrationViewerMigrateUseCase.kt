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

package com.algorand.android.ui.settings.migrationviewer

import com.algorand.android.models.Account.Type
import com.algorand.android.models.AccountCreation
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.usecase.GetLocalAccountsFromSharedPrefUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import javax.inject.Inject

class MigrationViewerMigrateUseCase @Inject constructor(
    private val getLocalAccountsFromSharedPrefUseCase: GetLocalAccountsFromSharedPrefUseCase,
    private val aesPlatformManager: AESPlatformManager,
    private val accountAdditionUseCase: AccountAdditionUseCase
) {

    @SuppressWarnings("LongMethod")
    suspend fun invoke() {
        val localAccounts = getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        localAccounts?.forEach { localAccount ->
            var migrateAccount: AccountCreation? = null
            when (localAccount.type) {
                Type.STANDARD, Type.REKEYED, Type.REKEYED_AUTH -> {
                    localAccount.getSecretKey()?.let {
                        migrateAccount = AccountCreation(
                            address = localAccount.address,
                            customName = localAccount.name,
                            isBackedUp = localAccount.isBackedUp,
                            type = AccountCreation.Type.Algo25(
                                aesPlatformManager.encryptByteArray(it)
                            ),
                            creationType = CreationType.RECOVER
                        )
                    } ?: run {
                        migrateAccount = AccountCreation(
                            address = localAccount.address,
                            customName = localAccount.name,
                            isBackedUp = localAccount.isBackedUp,
                            type = AccountCreation.Type.NoAuth,
                            creationType = CreationType.WATCH
                        )
                    }
                }
                Type.LEDGER -> {
                    migrateAccount = AccountCreation(
                        address = localAccount.address,
                        customName = localAccount.name,
                        isBackedUp = localAccount.isBackedUp,
                        type = AccountCreation.Type.LedgerBle("", 0, ""),
                        creationType = CreationType.LEDGER
                    )
                }
                Type.WATCH -> {
                    migrateAccount = AccountCreation(
                        address = localAccount.address,
                        customName = localAccount.name,
                        isBackedUp = localAccount.isBackedUp,
                        type = AccountCreation.Type.NoAuth,
                        creationType = CreationType.WATCH
                    )
                }
                else -> { }
            }
            migrateAccount?.let { accountAdditionUseCase.addNewAccount(it) }
        }
    }
}
