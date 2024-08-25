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

package com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.selection.ui.usecase

import com.algorand.android.R
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.models.CreateAccount
import com.algorand.android.models.PluralAnnotatedString
import com.algorand.android.modules.basefoundaccount.selection.ui.mapper.BaseFoundAccountSelectionItemMapper
import com.algorand.android.modules.basefoundaccount.selection.ui.model.BaseFoundAccountSelectionItem
import com.algorand.android.modules.basefoundaccount.selection.ui.usecase.BaseFoundAccountSelectionItemUseCase
import com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.selection.ui.mapper.RekeyedAccountSelectionPreviewMapper
import com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.selection.ui.model.RekeyedAccountSelectionPreview
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.analytics.CreationType
import javax.inject.Inject

class RekeyedAccountSelectionPreviewUseCase @Inject constructor(
    private val rekeyedAccountSelectionPreviewMapper: RekeyedAccountSelectionPreviewMapper,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    baseFoundAccountSelectionItemMapper: BaseFoundAccountSelectionItemMapper
) : BaseFoundAccountSelectionItemUseCase(baseFoundAccountSelectionItemMapper) {

    suspend fun getRekeyedAccountSelectionPreviewFlow(
        rekeyedAccountAddresses: Array<String>
    ): RekeyedAccountSelectionPreview {
        val iconItem = createIconItem(R.drawable.ic_wallet)
        val titleItem = createTitleItem(
            PluralAnnotatedString(
                pluralStringResId = R.plurals.rekeyed_accounts_found,
                quantity = rekeyedAccountAddresses.count()
            )
        )
        val descriptionItem = createDescriptionItem(
            PluralAnnotatedString(
                pluralStringResId = R.plurals.you_have_rekeyed_accounts,
                quantity = rekeyedAccountAddresses.count()
            )
        )
        val baseFoundAccountSelectionItemList = mutableListOf<BaseFoundAccountSelectionItem>()
        baseFoundAccountSelectionItemList.add(iconItem)
        baseFoundAccountSelectionItemList.add(titleItem)
        baseFoundAccountSelectionItemList.add(descriptionItem)
        rekeyedAccountAddresses.forEach { rekeyedAccountAddress ->
            val accountDisplayName = getAccountDisplayName(rekeyedAccountAddress)
            val accountIconDrawablePreview = AccountIconDrawablePreview(
                backgroundColorResId = R.color.wallet_4,
                iconTintResId = R.color.wallet_4_icon,
                iconResId = R.drawable.ic_rekey_shield
            )
            val accountItem = createAccountItem(
                accountIconDrawablePreview = accountIconDrawablePreview,
                accountDisplayName = accountDisplayName,
                selectorDrawableRes = R.drawable.selector_found_account_checkbox,
                isSelected = false
            )
            baseFoundAccountSelectionItemList.add(accountItem)
        }
        return rekeyedAccountSelectionPreviewMapper.mapToRekeyedAccountSelectionPreview(
            isLoading = false,
            foundAccountSelectionListItem = baseFoundAccountSelectionItemList,
            primaryButtonTextResId = R.string.choose_accounts_to_add,
            secondaryButtonTextResId = R.string.skip_for_now,
            isPrimaryButtonEnable = false
        )
    }

    fun updatePreviewWithSelectedAccount(
        preview: RekeyedAccountSelectionPreview,
        selectedAccountAddress: String
    ): RekeyedAccountSelectionPreview {
        val updatedFoundAccountSelectionListItem = preview.foundAccountSelectionListItem.map { item ->
            if (item is BaseFoundAccountSelectionItem.AccountItem &&
                item.accountDisplayName.accountAddress == selectedAccountAddress
            ) {
                item.copy(isSelected = !item.isSelected)
            } else {
                item
            }
        }
        val isPrimaryButtonEnable = updatedFoundAccountSelectionListItem.any { item ->
            item is BaseFoundAccountSelectionItem.AccountItem && item.isSelected
        }
        val primaryButtonTextResId = if (isPrimaryButtonEnable) {
            R.string.add_rekeyed_accounts
        } else {
            R.string.choose_accounts_to_add
        }
        return preview.copy(
            foundAccountSelectionListItem = updatedFoundAccountSelectionListItem,
            isPrimaryButtonEnable = isPrimaryButtonEnable,
            primaryButtonTextResId = primaryButtonTextResId
        )
    }

    suspend fun updatePreviewWithChosenAccount(
        preview: RekeyedAccountSelectionPreview,
        accountCreation: CreateAccount
    ): RekeyedAccountSelectionPreview {
        preview.foundAccountSelectionListItem.forEach { item ->
            if (item is BaseFoundAccountSelectionItem.AccountItem && item.isSelected) {
                val isAccountLimitExceed = isAccountLimitExceedUseCase.isAccountLimitExceed()
                if (isAccountLimitExceed) {
                    return preview.copy(showAccountCountExceedErrorEvent = Event(Unit))
                }
                val rekeyedAccount = CreateAccount.NoAuth(
                    address = item.accountDisplayName.accountAddress,
                    customName = item.accountDisplayName.primaryDisplayName,
                    isBackedUp = false,
                    creationType = CreationType.REKEYED
                )
                accountAdditionUseCase.addNewAccount(rekeyedAccount)
            }
        }
        return preview.copy(navToNameRegistrationEvent = Event(accountCreation))
    }

    fun updatePreviewWithRecoveredAccount(
        preview: RekeyedAccountSelectionPreview,
        accountCreation: CreateAccount
    ): RekeyedAccountSelectionPreview {
        return preview.copy(navToNameRegistrationEvent = Event(accountCreation))
    }
}
