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
import com.algorand.backup.domain.model.RemovedAccount
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeedFirstAddresses
import javax.inject.Inject

internal class BackupAccountChangeProcessor @Inject constructor(
    private val getAllHdSeedFirstAddresses: GetAllHdSeedFirstAddresses
) {

    private var baselineAccounts: Map<String, LocalAccount>? = null
    private var baselineSeedFirstAddresses: Map<Int, String>? = null
    private var baselineCustomInfos: Map<String, CustomAccountInfo?>? = null

    fun reset() {
        baselineAccounts = null
        baselineSeedFirstAddresses = null
        baselineCustomInfos = null
    }

    suspend fun processAccountListChange(accounts: List<LocalAccount>): AccountChangeEvent {
        val currentMap = accounts.associateBy { it.algoAddress }
        val baseline = baselineAccounts

        baselineAccounts = currentMap
        baselineSeedFirstAddresses = getAllHdSeedFirstAddresses().associate { it.seedId to it.firstAddress }

        if (baseline == null) return AccountChangeEvent.InitialSnapshot

        val removedAddresses = baseline.keys - currentMap.keys
        val addedAddresses = currentMap.keys - baseline.keys

        if (addedAddresses.isEmpty() && removedAddresses.isEmpty()) return AccountChangeEvent.InitialSnapshot

        val removedAccounts = removedAddresses.mapNotNull { address ->
            val removedAccount = baseline[address] ?: return@mapNotNull null
            val secretsAddress = resolveSecretsAddress(removedAccount, currentMap.values)
            RemovedAccount(address, secretsAddress)
        }

        return AccountChangeEvent.AccountListChanged(addedAddresses, removedAccounts)
    }

    fun processCustomInfoChange(customInfos: Map<String, CustomAccountInfo?>): AccountChangeEvent {
        val baseline = baselineCustomInfos
        baselineCustomInfos = customInfos

        if (baseline == null) return AccountChangeEvent.InitialSnapshot

        val changedAddresses = customInfos.filter { (address, info) ->
            val baselineInfo = baseline[address]
            baselineInfo != null && baselineInfo != info
        }.keys

        if (changedAddresses.isEmpty()) return AccountChangeEvent.InitialSnapshot

        return AccountChangeEvent.CustomInfoChanged(changedAddresses)
    }

    private fun resolveSecretsAddress(
        removedAccount: LocalAccount,
        remainingAccounts: Collection<LocalAccount>
    ): String? {
        return when (removedAccount) {
            is LocalAccount.Algo25 -> removedAccount.algoAddress
            is LocalAccount.HdKey -> {
                val hasOtherKeysForSeed = remainingAccounts.any {
                    it is LocalAccount.HdKey && it.seedId == removedAccount.seedId
                }
                if (!hasOtherKeysForSeed) {
                    baselineSeedFirstAddresses?.get(removedAccount.seedId)
                } else null
            }
            is LocalAccount.LedgerBle,
            is LocalAccount.NoAuth,
            is LocalAccount.Joint -> null
        }
    }
}
