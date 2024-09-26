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

package com.algorand.android.modules.sorting.accountsorting.ui.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.module.account.core.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.module.account.core.ui.model.ButtonConfiguration
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.accountsorting.component.domain.usecase.GetAccountSortingTypeIdentifier
import com.algorand.android.accountsorting.component.domain.usecase.SaveAccountSortPreference
import com.algorand.android.accountsorting.component.domain.usecase.SetAccountOrderIndex
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.modules.sorting.accountsorting.domain.mapper.BaseSortingListItemMapper
import com.algorand.android.modules.sorting.accountsorting.domain.mapper.SortingPreviewMapper
import com.algorand.android.modules.sorting.accountsorting.domain.model.AccountSortingPreview
import com.algorand.android.modules.sorting.accountsorting.domain.model.BaseAccountSortingListItem
import com.algorand.android.modules.sorting.utils.SortingTypeCreator
import javax.inject.Inject

// TODO: 8.08.2022 Move account sorting feature to UI layer
@SuppressWarnings("LongParameterList")
open class AccountSortingPreviewUseCase @Inject constructor(
    private val baseSortingListItemMapper: BaseSortingListItemMapper,
    private val sortingTypeCreator: SortingTypeCreator,
    private val sortingPreviewMapper: SortingPreviewMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getSortedAccountsByPreference: GetSortedAccountsByPreference,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val setAccountOrderIndex: SetAccountOrderIndex,
    private val saveAccountSortPreference: SaveAccountSortPreference,
    private val getAccountSortingTypeIdentifier: GetAccountSortingTypeIdentifier
) {

    fun getInitialSortingPreview(): AccountSortingPreview {
        return sortingPreviewMapper.mapToInitialAccountSortingPreview()
    }

    suspend fun getAccountSortingPreference(): AccountSortingTypeIdentifier {
        return getAccountSortingTypeIdentifier()
    }

    suspend fun saveManuallySortedAccountList(baseAccountSortingList: List<BaseAccountSortingListItem>) {
        val sortedAccountAddresses = baseAccountSortingList.mapNotNull {
            if (it is BaseAccountSortingListItem.AccountSortListItem) {
                it.accountListItem.itemConfiguration.accountAddress
            } else {
                null
            }
        }
        sortedAccountAddresses.forEachIndexed { index, address ->
            setAccountOrderIndex(address, index)
        }
    }

    suspend fun saveSortingPreferences(sortingPreferences: AccountSortingTypeIdentifier) {
        saveAccountSortPreference(sortingPreferences)
    }

    fun swapItemsAndUpdateList(
        currentPreview: AccountSortingPreview,
        fromPosition: Int,
        toPosition: Int,
        sortingPreferences: AccountSortingTypeIdentifier
    ): AccountSortingPreview {
        val currentItemList = currentPreview.accountSortingListItems.toMutableList()
        val fromItem = currentItemList.removeAt(fromPosition)
        currentItemList.add(toPosition, fromItem)
        val sortTypeListItems = getSortTypeListItems(sortingPreferences)

        return sortingPreviewMapper.mapToAccountSortingPreview(
            sortTypeListItems = sortTypeListItems,
            accountSortingListItems = currentItemList
        )
    }

    suspend fun createSortingPreview(sortingIdentifier: AccountSortingTypeIdentifier): AccountSortingPreview {
        val sortTypeListItems = getSortTypeListItems(sortingIdentifier)
        val accountSortingListItems = getAccountSortingListItems(sortingIdentifier)
        val isAccountListVisible = sortingIdentifier == AccountSortingTypeIdentifier.MANUAL
        return sortingPreviewMapper.mapToAccountSortingPreview(
            sortTypeListItems = sortTypeListItems,
            accountSortingListItems = if (isAccountListVisible) accountSortingListItems else emptyList()
        )
    }

    private fun getSortTypeListItems(
        sortingIdentifier: AccountSortingTypeIdentifier
    ): List<BaseAccountSortingListItem.SortTypeListItem> {
        val sortingTypes = sortingTypeCreator.createForAccountSorting()
        return sortingTypes.map { sortingType ->
            baseSortingListItemMapper.mapToSortingTypeListItem(
                accountSortingTypeIdentifier = sortingType,
                isChecked = sortingType == sortingIdentifier
            )
        }
    }

    private suspend fun getAccountSortingListItems(
        sortingIdentifier: AccountSortingTypeIdentifier,
    ): MutableList<BaseAccountSortingListItem> {
        val headerListItem = createHeaderListItem()
        val accountSortListItems = createAccountSortListItems(sortingIdentifier)
        return mutableListOf<BaseAccountSortingListItem>().apply {
            add(headerListItem)
            addAll(accountSortListItems)
        }
    }

    private fun createHeaderListItem(): BaseAccountSortingListItem.HeaderListItem {
        return baseSortingListItemMapper.mapToHeaderListItem(R.string.reorganize_accounts_manually)
    }

    private suspend fun createAccountSortListItems(
        sortingIdentifier: AccountSortingTypeIdentifier
    ): List<BaseAccountSortingListItem.AccountSortListItem> {
        val accountListItems = getSortedAccountsByPreference(
            sortingIdentifier = sortingIdentifier,
            onLoadedAccountConfiguration = {
                val accountValue = getAccountTotalValue(address, true)
                accountItemConfigurationMapper(
                    accountAddress = address,
                    accountDisplayName = getAccountDisplayName(address),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(address),
                    accountType = accountType,
                    accountPrimaryValue = accountValue.primaryAccountValue,
                    dragButtonConfiguration = ButtonConfiguration(
                        iconDrawableResId = R.drawable.ic_reorder,
                        iconTintResId = R.color.text_gray_lighter,
                        iconBackgroundColorResId = R.color.transparent,
                        iconRippleColorResId = R.color.transparent
                    )
                )
            }, onFailedAccountConfiguration = {
                accountItemConfigurationMapper(
                    accountAddress = this,
                    accountDisplayName = getAccountDisplayName(this),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(this),
                    showWarningIcon = true
                )
            }
        )
        return accountListItems.map { sortedAccountListItem ->
            baseSortingListItemMapper.mapToAccountSortItem(accountListItem = sortedAccountListItem)
        }
    }
}
