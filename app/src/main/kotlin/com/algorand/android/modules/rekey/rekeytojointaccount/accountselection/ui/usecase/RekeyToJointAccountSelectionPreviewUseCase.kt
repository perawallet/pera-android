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

package com.algorand.android.modules.rekey.rekeytojointaccount.accountselection.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ScreenState
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountTotalValue
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.basesingleaccountselection.ui.mapper.SingleAccountSelectionListItemMapper
import com.algorand.android.modules.basesingleaccountselection.ui.model.SingleAccountSelectionListItem
import com.algorand.android.modules.basesingleaccountselection.ui.model.SingleAccountSelectionListItem.AccountItem
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.rekey.rekeytojointaccount.accountselection.ui.mapper.RekeyToJointAccountSelectionPreviewMapper
import com.algorand.android.modules.rekey.rekeytojointaccount.accountselection.ui.model.RekeyToJointAccountSelectionPreview
import com.algorand.android.utils.formatAsCurrency
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import javax.inject.Inject

@SuppressWarnings("LongParameterList")
class RekeyToJointAccountSelectionPreviewUseCase @Inject constructor(
    private val rekeyToJointAccountSelectionPreviewMapper: RekeyToJointAccountSelectionPreviewMapper,
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getAccountType: GetAccountType,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val singleAccountSelectionListItemMapper: SingleAccountSelectionListItemMapper,
) {

    fun getInitialRekeyToJointAccountSelectionPreview(): RekeyToJointAccountSelectionPreview {
        return rekeyToJointAccountSelectionPreviewMapper.mapToRekeyToJointAccountSelectionPreview(
            screenState = null,
            singleAccountSelectionListItems = emptyList(),
            isLoading = true
        )
    }

    suspend fun getRekeyToJointAccountSelectionPreview(
        address: String
    ): RekeyToJointAccountSelectionPreview {
        val accountItems = getSortedLocalAccounts().mapNotNull { accountOrderIndex ->
            val localAccountAddress = accountOrderIndex.address
            val isAccountEligible = isAccountEligibleToRekey(localAccountAddress, address)
            if (!isAccountEligible) return@mapNotNull null
            createAccountItemListFromAccountDetail(localAccountAddress)
        }
        val screenState = if (accountItems.isEmpty()) {
            ScreenState.CustomState(title = R.string.no_account_found)
        } else {
            null
        }
        val titleItem = singleAccountSelectionListItemMapper.mapToTitleItem(
            textResId = R.string.select_account
        )
        val descriptionItem = singleAccountSelectionListItemMapper.mapToDescriptionItem(
            descriptionAnnotatedString = AnnotatedString(
                stringResId = R.string.choose_the_joint_account_rekey
            )
        )
        val singleAccountSelectionListItems = mutableListOf<SingleAccountSelectionListItem>().apply {
            add(titleItem)
            add(descriptionItem)
            addAll(accountItems)
        }
        return rekeyToJointAccountSelectionPreviewMapper.mapToRekeyToJointAccountSelectionPreview(
            screenState = screenState,
            singleAccountSelectionListItems = singleAccountSelectionListItems,
            isLoading = false
        )
    }

    private suspend fun createAccountItemListFromAccountDetail(address: String): AccountItem {
        val selectedCurrencySymbol = getPrimaryCurrencySymbolOrName()
        val secondaryCurrencySymbol = getSecondaryCurrencySymbol()
        val isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo()
        val accountDisplayName = getAccountDisplayName(address)
        val accountValue = getAccountTotalValue(address, true)
        val accountFormattedPrimaryValue = accountValue.primaryAccountValue.formatAsCurrency(
            symbol = selectedCurrencySymbol,
            isCompact = true,
            isFiat = !isPrimaryCurrencyAlgo
        )
        val accountFormattedSecondaryValue = accountValue.secondaryAccountValue.formatAsCurrency(
            symbol = secondaryCurrencySymbol,
            isCompact = true,
            isFiat = !isPrimaryCurrencyAlgo
        )
        return singleAccountSelectionListItemMapper.mapToAccountItem(
            accountDisplayName = accountDisplayName,
            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
            accountFormattedPrimaryValue = accountFormattedPrimaryValue,
            accountFormattedSecondaryValue = accountFormattedSecondaryValue
        )
    }

    private suspend fun isAccountEligibleToRekey(
        localAccountAddress: String,
        accountAddress: String
    ): Boolean {
        val accountType = getAccountType(localAccountAddress)
        val isJointAccount = accountType is AccountType.Joint
        val isSameAddress = localAccountAddress == accountAddress
        return isJointAccount && !isSameAddress
    }
}
