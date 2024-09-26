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

package com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.decider

import com.algorand.android.R
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.designsystem.AnnotatedString
import javax.inject.Inject

class RekeyToStandardAccountIntroductionPreviewDecider @Inject constructor() {

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
            AccountType.Algo25 -> R.string.use_another_account_s_private
            AccountType.LedgerBle -> R.string.remove_a_ledger_device_from
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.string.rekey_your_account_to
            AccountType.NoAuth, null -> null
        }
        // TODO find a way to use `click spannable` in use case
        return AnnotatedString(stringResId = stringResId ?: return null)
    }

    fun decideExpectationListItems(accountType: AccountType?): List<AnnotatedString> {
        return mutableListOf<AnnotatedString>().apply {
            when (accountType) {
                AccountType.Algo25 -> {
                    add(AnnotatedString(stringResId = R.string.future_transactions_can_only))
                    add(AnnotatedString(stringResId = R.string.this_account_will_no_longer))
                    add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
                }
                AccountType.LedgerBle -> {
                    add(AnnotatedString(stringResId = R.string.future_transactions_can_only_be))
                    add(AnnotatedString(stringResId = R.string.your_ledger_device_will_no_longer))
                    add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
                    add(AnnotatedString(stringResId = R.string.make_sure_bluetooth))
                }
                AccountType.Rekeyed, AccountType.RekeyedAuth -> {
                    add(AnnotatedString(stringResId = R.string.future_transactions_will_be_signed))
                    add(AnnotatedString(stringResId = R.string.this_account_will_continue))
                    add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
                    add(AnnotatedString(stringResId = R.string.make_sure_bluetooth))
                }
                AccountType.NoAuth, null -> Unit
            }
        }
    }
}
