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

package com.algorand.android.modules.settings.ui.usecase

import com.algorand.android.modules.asb.util.AlgorandSecureBackupUtils
import com.algorand.android.modules.settings.ui.mapper.SettingsPreviewMapper
import com.algorand.android.modules.settings.ui.model.SettingsPreview
import com.algorand.wallet.account.custom.domain.usecase.GetBackedUpAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsPreviewUseCase @Inject constructor(
    private val settingsPreviewMapper: SettingsPreviewMapper,
    private val getBackedUpAccounts: GetBackedUpAccounts,
    private val getLocalAccounts: GetLocalAccounts
) {

    fun getSettingsPreviewFlow(): Flow<SettingsPreview> = flow {
        val backedUpAccounts = getBackedUpAccounts()
        emit(createSettingsPreview(backedUpAccounts))
    }

    private suspend fun createSettingsPreview(backedUpAccounts: Set<String>): SettingsPreview {
        val localAccounts = getLocalAccounts()
        val localAccountAddresses = localAccounts.map { it.algoAddress }
        val remainingAccounts = localAccountAddresses.filter { it !in backedUpAccounts }
        val eligibleLocalAccounts = remainingAccounts.filter { accountAddress ->
            val account = localAccounts.first { it.algoAddress == accountAddress }
            AlgorandSecureBackupUtils.isAccountEligible(account)
        }
        return settingsPreviewMapper.mapToSettingsPreview(
            isAlgorandSecureBackupDescriptionVisible = eligibleLocalAccounts.isNotEmpty(),
            notBackedUpAccountCounts = eligibleLocalAccounts.size
        )
    }
}
