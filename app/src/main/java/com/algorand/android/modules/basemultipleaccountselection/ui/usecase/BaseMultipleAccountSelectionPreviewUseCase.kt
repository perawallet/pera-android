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

package com.algorand.android.modules.basemultipleaccountselection.ui.usecase

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.module.account.core.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.customviews.TriStatesCheckBox
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ui.AccountAssetItemButtonState.CHECKED
import com.algorand.android.models.ui.AccountAssetItemButtonState.UNCHECKED
import com.algorand.android.modules.basemultipleaccountselection.ui.mapper.MultipleAccountSelectionListItemMapper
import com.algorand.android.modules.basemultipleaccountselection.ui.model.MultipleAccountSelectionListItem

open class BaseMultipleAccountSelectionPreviewUseCase(
    private val multipleAccountSelectionListItemMapper: MultipleAccountSelectionListItemMapper,
    private val getSortedAccountsByPreference: GetSortedAccountsByPreference,
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) {

    protected fun getSelectedAccountAddressList(
        multipleAccountSelectionList: List<MultipleAccountSelectionListItem>
    ): List<String> {
        return multipleAccountSelectionList.mapNotNull {
            if (it is MultipleAccountSelectionListItem.AccountItem && it.accountViewButtonState == CHECKED) {
                it.accountDisplayName.accountAddress
            } else {
                null
            }
        }
    }

    protected fun updateListItemAfterHeaderCheckBoxClicked(
        currentHeaderCheckBoxState: TriStatesCheckBox.CheckBoxState?,
        multipleAccountSelectionList: List<MultipleAccountSelectionListItem>
    ): List<MultipleAccountSelectionListItem> {
        val headerCheckBoxState = when (currentHeaderCheckBoxState) {
            TriStatesCheckBox.CheckBoxState.UNCHECKED -> TriStatesCheckBox.CheckBoxState.CHECKED
            TriStatesCheckBox.CheckBoxState.CHECKED -> TriStatesCheckBox.CheckBoxState.UNCHECKED
            TriStatesCheckBox.CheckBoxState.PARTIAL_CHECKED -> TriStatesCheckBox.CheckBoxState.CHECKED
            else -> TriStatesCheckBox.CheckBoxState.UNCHECKED
        }
        val accountItemCheckBoxState = if (headerCheckBoxState == TriStatesCheckBox.CheckBoxState.CHECKED) {
            CHECKED
        } else {
            UNCHECKED
        }
        return multipleAccountSelectionList.map { item ->
            when (item) {
                is MultipleAccountSelectionListItem.AccountItem -> {
                    item.copy(accountViewButtonState = accountItemCheckBoxState)
                }
                is MultipleAccountSelectionListItem.AccountHeaderItem -> {
                    item.copy(checkboxState = headerCheckBoxState)
                }
                else -> item
            }
        }
    }

    protected fun updateListItemAfterAccountCheckBoxClicked(
        accountAddress: String,
        multipleAccountSelectionList: List<MultipleAccountSelectionListItem>
    ): List<MultipleAccountSelectionListItem> {
        val updateAccountList = multipleAccountSelectionList.map { item ->
            when (item) {
                is MultipleAccountSelectionListItem.AccountItem -> {
                    if (item.accountDisplayName.accountAddress == accountAddress) {
                        val checkBoxState = if (item.accountViewButtonState == CHECKED) UNCHECKED else CHECKED
                        item.copy(accountViewButtonState = checkBoxState)
                    } else {
                        item
                    }
                }
                else -> item
            }
        }
        val accountItems = updateAccountList.filterIsInstance<MultipleAccountSelectionListItem.AccountItem>()
        val areAllAccountsChecked = accountItems.all { it.accountViewButtonState == CHECKED }
        val areAllAccountsUnchecked = accountItems.all { it.accountViewButtonState == UNCHECKED }
        val headerCheckBoxState = when {
            areAllAccountsChecked -> TriStatesCheckBox.CheckBoxState.CHECKED
            areAllAccountsUnchecked -> TriStatesCheckBox.CheckBoxState.UNCHECKED
            else -> TriStatesCheckBox.CheckBoxState.PARTIAL_CHECKED
        }
        return updateAccountList.map {
            if (it is MultipleAccountSelectionListItem.AccountHeaderItem) {
                it.copy(checkboxState = headerCheckBoxState)
            } else {
                it
            }
        }
    }

    protected fun createTitleItem(
        @StringRes textResId: Int
    ): MultipleAccountSelectionListItem.TitleItem {
        return multipleAccountSelectionListItemMapper.mapToTitleItem(textResId = textResId)
    }

    protected fun createDescriptionItem(
        annotatedString: AnnotatedString
    ): MultipleAccountSelectionListItem.DescriptionItem {
        return multipleAccountSelectionListItemMapper.mapToDescriptionItem(
            annotatedString = annotatedString
        )
    }

    protected fun createAccountHeaderItem(
        @PluralsRes titleRes: Int,
        accountCount: Int,
        checkboxState: TriStatesCheckBox.CheckBoxState,
    ): MultipleAccountSelectionListItem.AccountHeaderItem {
        return multipleAccountSelectionListItemMapper.mapToAccountHeaderItem(
            titleRes = titleRes,
            accountCount = accountCount,
            checkboxState = checkboxState
        )
    }

    protected suspend fun createAccountItemList(
        excludedAccountTypes: List<AccountType>
    ): List<MultipleAccountSelectionListItem.AccountItem> {
        return getSortedAccountsByPreference(
            excludedAccountTypes = excludedAccountTypes,
            onLoadedAccountConfiguration = {
                accountItemConfigurationMapper(
                    accountAddress = address,
                    accountDisplayName = getAccountDisplayName(address),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(address),
                    accountType = accountType,
                    showWarningIcon = true
                )
            },
            onFailedAccountConfiguration = {
                accountItemConfigurationMapper(
                    accountDisplayName = getAccountDisplayName.invoke(this),
                    accountAddress = this,
                    accountType = null,
                    accountIconDrawablePreview = getAccountIconDrawablePreview(this),
                )
            }
        ).mapNotNull { accountListItem ->
            with(accountListItem.itemConfiguration) {
                multipleAccountSelectionListItemMapper.mapToAccountItem(
                    accountDisplayName = accountDisplayName ?: return@mapNotNull null,
                    accountIconDrawablePreview = accountIconDrawablePreview ?: return@mapNotNull null,
                    accountViewButtonState = CHECKED
                )
            }
        }
    }
}
