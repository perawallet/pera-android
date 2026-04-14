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

import com.algorand.backup.domain.model.AccountChangeEvent
import com.algorand.backup.domain.model.BackupItemChange
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.custom.domain.usecase.GetAccountsCustomInfoFlow
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class AccountBackupItemObserver @Inject constructor(
    private val getLocalAccountsFlow: GetLocalAccountsFlow,
    private val getAccountsCustomInfoFlow: GetAccountsCustomInfoFlow,
    private val syncStateUpdater: BackupSyncStateUpdater,
    private val hasBackup: HasBackup,
    private val changeProcessor: BackupAccountChangeProcessor
) : BackupItemObserver {

    private var accountListSyncRequired = false

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeChanges(scope: CoroutineScope): Flow<BackupItemChange> {
        return getLocalAccountsFlow()
            .onEach { accounts -> accountListSyncRequired = processAccountListChange(accounts) }
            .flatMapLatest { accounts ->
                val addresses = accounts.map { it.algoAddress }
                getAccountsCustomInfoFlow(addresses)
            }
            .map { customInfos ->
                val customInfoResult = processCustomInfoChange(customInfos)
                if (accountListSyncRequired) {
                    accountListSyncRequired = false
                    BackupItemChange.SyncRequired
                } else {
                    customInfoResult
                }
            }
    }

    override fun reset() {
        accountListSyncRequired = false
        changeProcessor.reset()
    }

    private suspend fun processAccountListChange(accounts: List<LocalAccount>): Boolean {
        val event = changeProcessor.processAccountListChange(accounts)

        if (!hasBackup() || event !is AccountChangeEvent.AccountListChanged) return false

        val deleteKeys = event.removedAccounts.flatMap { removed ->
            buildList {
                add(BackupItemKey.accounts(removed.address))
                if (removed.secretsAddress != null) {
                    add(BackupItemKey.secrets(removed.secretsAddress))
                }
            }
        }.toSet()

        if (deleteKeys.isNotEmpty()) {
            syncStateUpdater.markPendingDelete(deleteKeys)
        }

        return true
    }

    private suspend fun processCustomInfoChange(customInfos: Map<String, CustomAccountInfo?>): BackupItemChange {
        val event = changeProcessor.processCustomInfoChange(customInfos)

        if (!hasBackup() || event !is AccountChangeEvent.CustomInfoChanged) {
            return BackupItemChange.NoChange
        }

        val dirtyKeys = event.changedAddresses.map { address ->
            BackupItemKey.accounts(address)
        }.toSet()

        syncStateUpdater.markDirty(dirtyKeys, BackupItemType.ACCOUNT)

        return BackupItemChange.SyncRequired
    }
}
