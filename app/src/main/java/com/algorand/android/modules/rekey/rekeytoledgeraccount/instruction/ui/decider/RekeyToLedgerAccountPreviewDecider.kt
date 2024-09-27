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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.instruction.ui.decider

import com.algorand.android.R
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.drawable.AnnotatedString
import javax.inject.Inject

class RekeyToLedgerAccountPreviewDecider @Inject constructor() {

    fun decideBannerDrawableResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> R.drawable.ic_rekey_from_standard_banner
            AccountType.LedgerBle -> R.drawable.ic_rekey_from_ledger_banner
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.drawable.ic_rekey_from_rekeyed_banner
            // [null] and [NoAuth] cases are not possible
            AccountType.NoAuth, null -> R.drawable.ic_rekey_from_rekeyed_banner
        }
    }

    fun decideDescriptionAnnotatedString(accountType: AccountType?): AnnotatedString? {
        val stringResId = when (accountType) {
            AccountType.Algo25 -> R.string.back_your_standard_account_with
            AccountType.LedgerBle -> R.string.change_the_ledger_account_signing
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.string.rekey_your_account_to_a_different_account
            AccountType.NoAuth, null -> null
        }
        // TODO find a way to use `click spannable` in use case
        return AnnotatedString(stringResId = stringResId ?: return null)
    }

    fun decideExpectationListItems(accountType: AccountType?): List<AnnotatedString> {
        return mutableListOf<AnnotatedString>().apply {
            when (accountType) {
                AccountType.Algo25 -> {
                    add(AnnotatedString(stringResId = R.string.future_transactions_will_be))
                    add(AnnotatedString(stringResId = R.string.this_account_will_no_longer))
                    add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
                    add(AnnotatedString(stringResId = R.string.make_sure_bluetooth))
                }
                AccountType.LedgerBle -> {
                    add(AnnotatedString(stringResId = R.string.future_transactions_will_be))
                    add(AnnotatedString(stringResId = R.string.ff_you_already_have_a_ledger))
                    add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
                    add(AnnotatedString(stringResId = R.string.make_sure_bluetooth))
                }
                AccountType.Rekeyed, AccountType.RekeyedAuth -> {
                    add(AnnotatedString(stringResId = R.string.future_transactions_will_be_signed_by))
                    add(AnnotatedString(stringResId = R.string.this_account_will_continue))
                    add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
                    add(AnnotatedString(stringResId = R.string.make_sure_bluetooth))
                }
                AccountType.NoAuth, null -> Unit
            }
        }
    }
}
