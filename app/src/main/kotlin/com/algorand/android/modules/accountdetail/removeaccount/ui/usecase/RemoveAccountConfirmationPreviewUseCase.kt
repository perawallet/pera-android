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

package com.algorand.android.modules.accountdetail.removeaccount.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.PluralAnnotatedString
import com.algorand.android.modules.accountdetail.removeaccount.ui.mapper.RemoveAccountConfirmationPreviewMapper
import com.algorand.android.modules.accountdetail.removeaccount.ui.model.RemoveAccountConfirmationPreview
import com.algorand.android.utils.Event
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.detail.domain.usecase.GetLocalRekeyedAccountCount
import javax.inject.Inject

class RemoveAccountConfirmationPreviewUseCase @Inject constructor(
    private val removeAccountConfirmationPreviewMapper: RemoveAccountConfirmationPreviewMapper,
    private val getLocalRekeyedAccountCount: GetLocalRekeyedAccountCount,
    private val getAccountType: GetAccountType
) {

    fun getRemoveAccountConfirmationPreview(): RemoveAccountConfirmationPreview {
        return removeAccountConfirmationPreviewMapper.mapToRemoveAccountConfirmationPreview()
    }

    suspend fun updatePreviewWithDescriptionText(
        preview: RemoveAccountConfirmationPreview,
        accountAddress: String
    ): RemoveAccountConfirmationPreview {
        val accountType = getAccountType(accountAddress)
        val descriptionTextResId = when (accountType) {
            AccountType.NoAuth -> R.string.you_are_about_to_remove_watch_account
            else -> R.string.you_are_about_to_remove_account
        }

        return preview.copy(
            descriptionTextResId = Event(descriptionTextResId)
        )
    }

    suspend fun updatePreviewWithRemoveAccountConfirmation(
        preview: RemoveAccountConfirmationPreview,
        accountAddress: String
    ): RemoveAccountConfirmationPreview {
        val accountType = getAccountType(accountAddress)
        if (accountType == AccountType.NoAuth) {
            return preview.copy(navBackEvent = Event(true))
        }

        val rekeyedAccountCount = getLocalRekeyedAccountCount(accountAddress)
        val hasAccountAnyRekeyedAccount = rekeyedAccountCount > 0

        if (!hasAccountAnyRekeyedAccount) {
            return preview.copy(navBackEvent = Event(true))
        }

        return preview.copy(
            showGlobalErrorEvent = Event(
                PluralAnnotatedString(
                    pluralStringResId = R.plurals.you_can_t_remove_this_account,
                    quantity = rekeyedAccountCount
                )
            ),
            navBackEvent = Event(false)
        )
    }
}
