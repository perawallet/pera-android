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

import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeedFirstAddresses
import javax.inject.Inject

internal class DefaultResolveAddedAccountBackupKeys @Inject constructor(
    private val getAllHdSeedFirstAddresses: GetAllHdSeedFirstAddresses
) : ResolveAddedAccountBackupKeys {

    override suspend fun invoke(addedAddresses: Set<String>, accounts: List<LocalAccount>): Set<BackupItemKey> {
        if (addedAddresses.isEmpty()) return emptySet()

        val addedAccounts = findAddedAccounts(addedAddresses, accounts)
        val seedFirstAddresses = loadSeedFirstAddresses(addedAccounts)

        return addedAccounts.flatMapTo(mutableSetOf()) { account ->
            backupKeysFor(account, accounts, seedFirstAddresses)
        }
    }

    private fun findAddedAccounts(addedAddresses: Set<String>, accounts: List<LocalAccount>): List<LocalAccount> {
        return addedAddresses.mapNotNull { address ->
            accounts.firstOrNull { it.algoAddress == address }
        }
    }

    private suspend fun loadSeedFirstAddresses(addedAccounts: List<LocalAccount>): Map<Int, String> {
        val hasHdKeyAccount = addedAccounts.any { it is LocalAccount.HdKey }
        if (!hasHdKeyAccount) return emptyMap()
        return getAllHdSeedFirstAddresses().associate { it.seedId to it.firstAddress }
    }

    private fun backupKeysFor(
        account: LocalAccount,
        allAccounts: List<LocalAccount>,
        seedFirstAddresses: Map<Int, String>
    ): Set<BackupItemKey> {
        val accountKey = BackupItemKey.accounts(account.algoAddress)
        val secretsKey = secretsKeyFor(account, allAccounts, seedFirstAddresses)
        return setOfNotNull(accountKey, secretsKey)
    }

    private fun secretsKeyFor(
        account: LocalAccount,
        allAccounts: List<LocalAccount>,
        seedFirstAddresses: Map<Int, String>
    ): BackupItemKey? {
        return when (account) {
            is LocalAccount.Algo25 -> BackupItemKey.secrets(account.algoAddress)
            is LocalAccount.HdKey -> hdKeySecretsKey(account, allAccounts, seedFirstAddresses)
            is LocalAccount.LedgerBle,
            is LocalAccount.NoAuth,
            is LocalAccount.Joint -> null
        }
    }

    private fun hdKeySecretsKey(
        account: LocalAccount.HdKey,
        allAccounts: List<LocalAccount>,
        seedFirstAddresses: Map<Int, String>
    ): BackupItemKey? {
        if (!isOnlyAccountForSeed(account, allAccounts)) return null
        val seedFirstAddress = seedFirstAddresses[account.seedId] ?: return null
        return BackupItemKey.secrets(seedFirstAddress)
    }

    private fun isOnlyAccountForSeed(account: LocalAccount.HdKey, allAccounts: List<LocalAccount>): Boolean {
        return allAccounts.none { other ->
            other is LocalAccount.HdKey && other.seedId == account.seedId && other.algoAddress != account.algoAddress
        }
    }
}
