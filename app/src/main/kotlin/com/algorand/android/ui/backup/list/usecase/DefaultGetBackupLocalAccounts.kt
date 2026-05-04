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

package com.algorand.android.ui.backup.list.usecase

import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.ui.backup.list.mapper.BackupAccountIconPreviewMapper
import com.algorand.android.ui.backup.list.model.BackupLocalAccountItem
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class DefaultGetBackupLocalAccounts @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val getAccountLite: GetAccountLite,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val backupAccountIconPreviewMapper: BackupAccountIconPreviewMapper
) : GetBackupLocalAccounts {

    override suspend fun invoke(): List<BackupLocalAccountItem> {
        return getLocalAccounts().map { account ->
            val accountLite = getAccountLite(account.algoAddress)
            val cachedInfo = accountLite?.cachedInfo
            BackupLocalAccountItem(
                displayName = accountLite?.customName ?: account.algoAddress.toShortenedAddress(),
                address = account.algoAddress,
                iconPreview = backupAccountIconPreviewMapper.mapFromLocalAccount(account),
                isBackedUp = accountLite?.isBackedUp ?: false,
                assetCount = cachedInfo?.assetCount,
                formattedBalance = cachedInfo?.primaryAccountValue?.let { value ->
                    getCompactPrimaryAmountRenderer(PeraAmount(value), RenderType.Plain).getDisplayValue()
                }
            )
        }
    }
}
