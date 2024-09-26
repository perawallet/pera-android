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

package com.algorand.android.modules.accounticon.ui.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.model.AccountIconResource
import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import javax.inject.Inject

class CreateAccountOriginalStateIconDrawableUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail
) {

    suspend operator fun invoke(accountAddress: String): AccountIconDrawablePreview {
        val accountDetail = getAccountDetail(accountAddress)
        val accountIconResId = getAccountIconResId(accountDetail.accountRegistrationType)
        val accountIconTintResId = getAccountIconTintResId(accountDetail.accountRegistrationType)
        val accountIconBackgroundColorResId = getAccountIconBackgroundColorResId(accountDetail.accountRegistrationType)
        return AccountIconDrawablePreview(
            backgroundColorResId = accountIconBackgroundColorResId,
            iconResId = accountIconResId,
            iconTintResId = accountIconTintResId
        )
    }

    private fun getAccountIconBackgroundColorResId(registrationType: AccountRegistrationType?): Int {
        return when (registrationType) {
            AccountRegistrationType.LedgerBle -> AccountIconResource.LEDGER.backgroundColorResId
            AccountRegistrationType.NoAuth -> AccountIconResource.WATCH.backgroundColorResId
            AccountRegistrationType.Algo25 -> AccountIconResource.STANDARD.backgroundColorResId
            null -> R.color.layer_gray_lighter
        }
    }

    private fun getAccountIconTintResId(registrationType: AccountRegistrationType?): Int {
        return when (registrationType) {
            AccountRegistrationType.LedgerBle -> AccountIconResource.LEDGER.iconTintResId
            AccountRegistrationType.NoAuth -> AccountIconResource.WATCH.iconTintResId
            AccountRegistrationType.Algo25 -> AccountIconResource.STANDARD.iconTintResId
            null -> R.color.text_gray_lighter
        }
    }

    private fun getAccountIconResId(registrationType: AccountRegistrationType?): Int {
        return when (registrationType) {
            AccountRegistrationType.LedgerBle -> AccountIconResource.LEDGER.iconResId
            AccountRegistrationType.NoAuth -> AccountIconResource.WATCH.iconResId
            AccountRegistrationType.Algo25 -> AccountIconResource.STANDARD.iconResId
            null -> R.drawable.ic_question
        }
    }
}
