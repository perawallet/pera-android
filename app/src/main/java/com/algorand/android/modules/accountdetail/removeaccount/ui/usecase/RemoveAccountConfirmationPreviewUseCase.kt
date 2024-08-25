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

package com.algorand.android.modules.accountdetail.removeaccount.ui.usecase

import com.algorand.android.R
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.core.component.detail.domain.usecase.GetAccountType
import com.algorand.android.core.component.domain.usecase.GetRekeyedAccountAddresses
import com.algorand.android.core.component.domain.usecase.HasAccountAnyRekeyedAccount
import com.algorand.android.models.PluralAnnotatedString
import com.algorand.android.modules.accountdetail.removeaccount.ui.mapper.RemoveAccountConfirmationPreviewMapper
import com.algorand.android.modules.accountdetail.removeaccount.ui.model.RemoveAccountConfirmationPreview
import com.algorand.android.utils.Event
import javax.inject.Inject

class RemoveAccountConfirmationPreviewUseCase @Inject constructor(
    private val removeAccountConfirmationPreviewMapper: RemoveAccountConfirmationPreviewMapper,
    private val hasAccountAnyRekeyedAccount: HasAccountAnyRekeyedAccount,
    private val getRekeyedAccountAddresses: GetRekeyedAccountAddresses,
    private val getAccountType: GetAccountType,
    private val getAccountDetail: GetAccountDetail
) {

    suspend fun getRemoveAccountConfirmationPreview(address: String): RemoveAccountConfirmationPreview {
        val descriptionResId = getDescriptionResId(address)
        return removeAccountConfirmationPreviewMapper.mapToRemoveAccountConfirmationPreview(descriptionResId)
    }

    private suspend fun getDescriptionResId(accountAddress: String): Int {
        return when (getAccountType(accountAddress)) {
            AccountType.Algo25, AccountType.LedgerBle, AccountType.Rekeyed, AccountType.RekeyedAuth -> {
                R.string.you_are_about_to_remove_account
            }

            AccountType.NoAuth -> R.string.you_are_about_to_remove_watch_account
            else -> R.string.you_are_about_to_remove_account
        }
    }

    suspend fun updatePreviewWithRemoveAccountConfirmation(
        preview: RemoveAccountConfirmationPreview?,
        accountAddress: String
    ): RemoveAccountConfirmationPreview? {
        if (preview == null) return null
        val accountType = getAccountDetail(accountAddress).accountType
        if (accountType == AccountType.NoAuth) {
            return preview.copy(navBackEvent = Event(true))
        }

        val hasAccountAnyRekeyedAccount = hasAccountAnyRekeyedAccount(accountAddress)
        if (!hasAccountAnyRekeyedAccount) {
            return preview.copy(navBackEvent = Event(true))
        }

        val rekeyedAccountAddresses = getRekeyedAccountAddresses(accountAddress)

        return preview.copy(
            showGlobalErrorEvent = Event(
                PluralAnnotatedString(
                    pluralStringResId = R.plurals.you_can_t_remove_this_account,
                    quantity = rekeyedAccountAddresses.count()
                )
            ),
            navBackEvent = Event(false)
        )
    }
}
