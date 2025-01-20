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

package com.algorand.android.modules.accountcore.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import javax.inject.Inject

internal class GetAccountIconDrawablePreviewUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail
) : GetAccountIconDrawablePreview {

    override suspend fun invoke(address: String): AccountIconDrawablePreview {
        val accountDetail = getAccountDetail(address)
        return getAccountIconDrawablePreview(accountDetail)
    }

    override suspend fun invoke(accountDetail: AccountDetail): AccountIconDrawablePreview {
        return getAccountIconDrawablePreview(accountDetail)
    }

    private fun getAccountIconDrawablePreview(accountDetail: AccountDetail): AccountIconDrawablePreview {
        val accountIconResId = getAccountIconResId(accountDetail.accountType)
        val accountIconTintResId = getAccountIconTintResId(accountDetail.accountType)
        val accountIconBackgroundColorResId = getAccountIconBackgroundColorResId(accountDetail.accountType)
        return AccountIconDrawablePreview(
            backgroundColorResId = accountIconBackgroundColorResId,
            iconResId = accountIconResId,
            iconTintResId = accountIconTintResId
        )
    }

    private fun getAccountIconBackgroundColorResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconResource.STANDARD.backgroundColorResId
            AccountType.LedgerBle -> AccountIconResource.LEDGER.backgroundColorResId
            AccountType.Rekeyed -> R.color.negative_lighter
            AccountType.RekeyedAuth -> AccountIconResource.REKEYED.backgroundColorResId
            AccountType.NoAuth -> AccountIconResource.WATCH.backgroundColorResId
            AccountType.HdKey -> R.color.layer_gray_lighter // TODO
            null -> R.color.layer_gray_lighter
        }
    }

    private fun getAccountIconTintResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconResource.STANDARD.iconTintResId
            AccountType.Rekeyed -> R.color.negative
            AccountType.LedgerBle -> AccountIconResource.LEDGER.iconTintResId
            AccountType.RekeyedAuth -> AccountIconResource.LEDGER.iconTintResId
            AccountType.NoAuth -> AccountIconResource.WATCH.iconTintResId
            AccountType.HdKey -> R.color.text_gray // TODO
            null -> R.color.text_gray
        }
    }

    private fun getAccountIconResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconResource.STANDARD.iconResId
            AccountType.LedgerBle -> AccountIconResource.LEDGER.iconResId
            AccountType.NoAuth -> AccountIconResource.WATCH.iconResId
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.drawable.ic_rekey_shield
            AccountType.HdKey -> R.drawable.ic_wallet // TODO
            null -> AccountIconResource.STANDARD.iconResId
        }
    }
}
