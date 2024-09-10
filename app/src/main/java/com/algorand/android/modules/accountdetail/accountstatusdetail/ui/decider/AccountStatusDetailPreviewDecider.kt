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

package com.algorand.android.modules.accountdetail.accountstatusdetail.ui.decider

import android.content.Context
import com.algorand.android.R
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ui.AccountAssetItemButtonState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AccountStatusDetailPreviewDecider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun decideTitleString(accountType: AccountType?): String {
        val typeResId = when (accountType) {
            AccountType.LedgerBle -> R.string.ledger
            AccountType.NoAuth -> R.string.watch
            AccountType.Algo25 -> R.string.standard
            AccountType.RekeyedAuth -> R.string.rekeyed
            AccountType.Rekeyed, null -> R.string.no_auth
        }
        val accountTypeString = context.getString(typeResId)
        return context.getString(R.string.account_type_account, accountTypeString)
    }

    fun decideAccountTypeString(accountDetail: AccountDetail): String {
        val accountTypeString = when (accountDetail.accountType) {
            AccountType.LedgerBle -> context.getString(R.string.ledger)
            AccountType.NoAuth -> context.getString(R.string.watch)
            AccountType.Algo25 -> context.getString(R.string.standard)
            AccountType.Rekeyed -> context.getString(R.string.no_auth)
            AccountType.RekeyedAuth -> {
                val hasValidSecretKey = accountDetail.accountRegistrationType == AccountRegistrationType.Algo25
                val accountOriginalState = if (hasValidSecretKey) R.string.standard else R.string.unknown
                val accountStateString = context.getString(R.string.rekeyed)
                val accountOriginalStateString = context.getString(accountOriginalState)
                val authAccountState = context.getString(R.string.standard)
                context.getString(
                    R.string.account_state_transition,
                    accountStateString,
                    accountOriginalStateString,
                    authAccountState
                )
            }
            null -> context.getString(R.string.no_auth)
        }
        return accountTypeString
    }

    fun decideDescriptionAnnotatedString(accountDetail: AccountDetail): AnnotatedString {
        val descriptionStringResId = when (accountDetail.accountType) {
            AccountType.LedgerBle -> R.string.your_account_is_a_Ledger
            AccountType.NoAuth -> R.string.this_account_was_not
            AccountType.Algo25 -> R.string.your_account_is_a_standard
            AccountType.Rekeyed -> R.string.your_account_is_rekeyed_to_an
            AccountType.RekeyedAuth -> {
                val hasValidSecretKey = accountDetail.accountRegistrationType == AccountRegistrationType.Algo25
                if (hasValidSecretKey) {
                    R.string.your_account_is_rekeyed_to_another
                } else {
                    R.string.no_record_of_original_account
                }
            }
            null -> R.string.your_account_is_rekeyed_to_an
        }
        return AnnotatedString(descriptionStringResId)
    }

    fun decideAuthAccountActionButtonState(accountType: AccountType?): AccountAssetItemButtonState? {
        return when (accountType) {
            AccountType.LedgerBle, AccountType.NoAuth -> null
            AccountType.Algo25 -> null
            AccountType.RekeyedAuth -> AccountAssetItemButtonState.UNDO_REKEY
            AccountType.Rekeyed, null -> AccountAssetItemButtonState.WARNING
        }
    }
}
