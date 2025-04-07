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
import com.algorand.android.models.AccountIconResource.LEDGER
import com.algorand.android.models.AccountIconResource.STANDARD
import com.algorand.android.models.AccountIconResource.WATCH
import com.algorand.android.models.AccountIconResource.HD
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import javax.inject.Inject

internal class GetAccountOriginalStateIconDrawablePreviewUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail
) : GetAccountOriginalStateIconDrawablePreview {
    override suspend fun invoke(address: String): AccountIconDrawablePreview {
        val accountDetail = getAccountDetail(address)
        val accountIconResId = getAccountIconResId(accountDetail)
        val accountIconTintResId = getAccountIconTintResId(accountDetail)
        val accountIconBackgroundColorResId = getAccountIconBackgroundColorResId(accountDetail)
        return AccountIconDrawablePreview(
            backgroundColorResId = accountIconBackgroundColorResId,
            iconResId = accountIconResId,
            iconTintResId = accountIconTintResId
        )
    }

    private fun getAccountIconBackgroundColorResId(accountDetail: AccountDetail?): Int {
        return when (accountDetail?.accountType) {
            AccountType.LedgerBle -> LEDGER.backgroundColorResId
            AccountType.NoAuth -> WATCH.backgroundColorResId
            AccountType.Algo25 -> STANDARD.backgroundColorResId
            AccountType.HdKey -> HD.backgroundColorResId
            AccountType.RekeyedAuth, AccountType.Rekeyed, null -> {
                if (accountDetail?.canSignTransaction() == true) {
                    STANDARD.backgroundColorResId
                } else {
                    R.color.layer_gray_lighter
                }
            }
        }
    }

    private fun getAccountIconTintResId(accountDetail: AccountDetail?): Int {
        return when (accountDetail?.accountType) {
            AccountType.LedgerBle -> LEDGER.iconTintResId
            AccountType.NoAuth -> WATCH.iconTintResId
            AccountType.Algo25 -> STANDARD.iconTintResId
            AccountType.HdKey -> HD.iconTintResId
            AccountType.RekeyedAuth, AccountType.Rekeyed, null -> {
                if (accountDetail?.canSignTransaction() == true) STANDARD.iconTintResId else R.color.text_gray_lighter
            }
        }
    }

    private fun getAccountIconResId(accountDetail: AccountDetail?): Int {
        return when (accountDetail?.accountType) {
            AccountType.LedgerBle -> LEDGER.iconResId
            AccountType.NoAuth -> WATCH.iconResId
            AccountType.Algo25 -> STANDARD.iconResId
            AccountType.HdKey -> HD.iconResId
            AccountType.RekeyedAuth, AccountType.Rekeyed, null -> {
                if (accountDetail?.canSignTransaction() == true) STANDARD.iconResId else R.drawable.ic_question
            }
        }
    }
}
