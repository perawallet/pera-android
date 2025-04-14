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

package com.algorand.android.modules.accounts.domain.usecase

import com.algorand.android.R
import com.algorand.android.mapper.AccountSummaryMapper
import com.algorand.android.models.AccountDetailSummary
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import javax.inject.Inject

class AccountDetailSummaryUseCase @Inject constructor(
    private val accountSummaryMapper: AccountSummaryMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawable: GetAccountIconDrawablePreview,
    private val getAccountType: GetAccountType
) {

    suspend fun getAccountDetailSummary(address: String): AccountDetailSummary? {
        val accountType = getAccountType(address) ?: return null
        return accountSummaryMapper.mapToAccountDetailSummary(
            accountDisplayName = getAccountDisplayName(address),
            accountAddress = address,
            accountIconDrawablePreview = getAccountIconDrawable(address),
            accountTypeResId = getAccountTypeResId(accountType),
            shouldDisplayAccountType = shouldDisplayAccountType(accountType)
        )
    }

    private fun getAccountTypeResId(type: AccountType): Int {
        return when (type) {
            AccountType.Algo25 -> R.string.standard
            AccountType.LedgerBle -> R.string.ledger
            AccountType.NoAuth -> R.string.watch
            AccountType.Rekeyed -> R.string.rekeyed
            AccountType.RekeyedAuth -> R.string.rekeyed
            AccountType.HdKey -> R.string.hd_account
        }
    }

    private fun shouldDisplayAccountType(type: AccountType): Boolean {
        return when (type) {
            AccountType.LedgerBle, AccountType.NoAuth -> false
            AccountType.Algo25 -> false
            AccountType.Rekeyed, AccountType.RekeyedAuth -> true
            AccountType.HdKey -> false
        }
    }
}
