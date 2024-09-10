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

import com.algorand.android.accountcore.domain.utils.AlgorandSecureBackUpUtils
import com.algorand.android.asb.component.domain.usecase.GetBackedUpAccountAddressesFlow
import com.algorand.android.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.modules.settings.ui.mapper.SettingsPreviewMapper
import com.algorand.android.modules.settings.ui.model.SettingsPreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreviewUseCase @Inject constructor(
    private val settingsPreviewMapper: SettingsPreviewMapper,
    private val getBackedUpAccountAddressesFlow: GetBackedUpAccountAddressesFlow,
    private val getAccountsDetail: GetAccountsDetail
) {

    fun getSettingsPreviewFlow(): Flow<SettingsPreview> = getBackedUpAccountAddressesFlow().map { accounts ->
        createSettingsPreview(accounts)
    }

    private suspend fun createSettingsPreview(backedUpAccounts: Set<String>): SettingsPreview {
        val accountDetails = getAccountsDetail()
        val localAccountAddresses = accountDetails.map { it.address }
        val remainingAccounts = localAccountAddresses - backedUpAccounts
        val eligibleLocalAccounts = remainingAccounts.filter { accountAddress ->
            val account = accountDetails.firstOrNull { it.address == accountAddress } ?: return@filter false
            AlgorandSecureBackUpUtils.ELIGIBLE_ACCOUNT_TYPES.contains(account.accountType)
        }
        return settingsPreviewMapper.mapToSettingsPreview(
            isAlgorandSecureBackupDescriptionVisible = eligibleLocalAccounts.isNotEmpty(),
            notBackedUpAccountCounts = eligibleLocalAccounts.size
        )
    }
}
