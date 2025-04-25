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

package com.algorand.android.modules.accountcore.ui.usecase

import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDetailSummary
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import javax.inject.Inject

internal class GetAccountDetailSummaryUseCase @Inject constructor(
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountDetail: GetAccountDetail
) : GetAccountDetailSummary {

    override suspend fun invoke(address: String): AccountDetailSummary {
        val accountDetail = getAccountDetail(address)
        return AccountDetailSummary(
            address = address,
            accountIconDrawable = getAccountIconDrawablePreview(address),
            accountDisplayName = getAccountDisplayName(address),
            accountTypeResId = getAccountTypeResId(accountDetail.accountType),
            shouldDisplayAccountType = shouldDisplayAccountType(accountDetail.accountType),
            accountDetail = accountDetail
        )
    }

    override suspend fun invoke(accountDetail: AccountDetail): AccountDetailSummary {
        return with(accountDetail) {
            AccountDetailSummary(
                address = address,
                accountIconDrawable = getAccountIconDrawablePreview(address),
                accountDisplayName = getAccountDisplayName(address),
                accountTypeResId = getAccountTypeResId(accountType),
                shouldDisplayAccountType = shouldDisplayAccountType(accountType),
                accountDetail = accountDetail
            )
        }
    }

    private fun shouldDisplayAccountType(type: AccountType?): Boolean {
        return when (type) {
            AccountType.LedgerBle, AccountType.NoAuth, AccountType.Algo25, AccountType.HdKey -> false
            AccountType.Rekeyed, AccountType.RekeyedAuth, null -> true
        }
    }

    private fun getAccountTypeResId(type: AccountType?): Int {
        return when (type) {
            AccountType.LedgerBle -> R.string.ledger
            AccountType.NoAuth -> R.string.watch
            AccountType.Algo25 -> R.string.standard
            AccountType.Rekeyed, null -> R.string.no_auth
            AccountType.RekeyedAuth -> R.string.rekeyed
            AccountType.HdKey -> R.string.hd_account
        }
    }
}
