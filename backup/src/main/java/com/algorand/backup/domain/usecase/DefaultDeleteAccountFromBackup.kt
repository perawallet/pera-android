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

package com.algorand.backup.domain.usecase

import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeedFirstAddresses
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class DefaultDeleteAccountFromBackup @Inject constructor(
    private val getBackupId: GetBackupId,
    private val getLocalAccounts: GetLocalAccounts,
    private val getAllHdSeedFirstAddresses: GetAllHdSeedFirstAddresses,
    private val getAddressBackupSnapshot: GetAddressBackupSnapshot,
    private val deleteBackupItem: DeleteBackupItem
) : DeleteAccountFromBackup {

    override suspend fun invoke(address: String, deleteFromServer: Boolean) {
        val backupId = getBackupId() ?: return
        deleteBackupItem(backupId, BackupItemKey.accounts(address), deleteFromServer)

        val accounts = getLocalAccounts()
        val secretsAddress = accounts.firstOrNull { it.algoAddress == address }
            ?.let { account -> resolveSecretsAddress(account, accounts) }
            ?: resolveSecretsAddressFromBackup(address)
        if (secretsAddress != null) {
            deleteBackupItem(backupId, BackupItemKey.secrets(secretsAddress), deleteFromServer)
        }
    }

    private suspend fun resolveSecretsAddress(account: LocalAccount, allAccounts: List<LocalAccount>): String? {
        return when (account) {
            is LocalAccount.Algo25 -> account.algoAddress
            is LocalAccount.HdKey -> resolveHdSeedFirstAddressOrNull(account, allAccounts)
            is LocalAccount.LedgerBle,
            is LocalAccount.NoAuth,
            is LocalAccount.Joint -> null
        }
    }

    private suspend fun resolveHdSeedFirstAddressOrNull(
        account: LocalAccount.HdKey,
        allAccounts: List<LocalAccount>
    ): String? {
        val hasOtherKeysForSeed = allAccounts.any { other ->
            other is LocalAccount.HdKey && other.seedId == account.seedId && other.algoAddress != account.algoAddress
        }
        if (hasOtherKeysForSeed) {
            return null
        }
        return getAllHdSeedFirstAddresses().firstOrNull { it.seedId == account.seedId }?.firstAddress
    }

    private suspend fun resolveSecretsAddressFromBackup(address: String): String? {
        return when (val payload = getAddressBackupSnapshot().firstOrNull { it.address == address }) {
            is AddressBackupPayload.Algo25 -> payload.address
            is AddressBackupPayload.HdKey -> payload.seedFirstDerivedAddress
            is AddressBackupPayload.HdSeed -> payload.address
            is AddressBackupPayload.LedgerBle,
            is AddressBackupPayload.NoAuth,
            is AddressBackupPayload.Joint,
            null -> null
        }
    }
}
