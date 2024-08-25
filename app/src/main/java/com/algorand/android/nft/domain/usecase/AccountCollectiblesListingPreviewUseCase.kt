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

package com.algorand.android.nft.domain.usecase

import com.algorand.android.accountinfo.component.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.core.component.detail.domain.model.*
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetailFlow
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.core.component.domain.usecase.GetAccountCollectibleDataFlow
import com.algorand.android.models.*
import com.algorand.android.modules.collectibles.filter.domain.usecase.*
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.modules.collectibles.listingviewtype.domain.usecase.*
import com.algorand.android.modules.sorting.nftsorting.ui.usecase.CollectibleItemSortUseCase
import com.algorand.android.nft.mapper.CollectibleListingItemMapper
import com.algorand.android.nft.ui.model.*
import com.algorand.android.repository.FailedAssetRepository
import com.algorand.android.usecase.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@SuppressWarnings("LongParameterList")
class AccountCollectiblesListingPreviewUseCase @Inject constructor(
    private val collectibleListingItemMapper: CollectibleListingItemMapper,
    private val failedAssetRepository: FailedAssetRepository,
    private val collectibleItemSortUseCase: CollectibleItemSortUseCase,
    private val getNFTListingViewTypePreferenceUseCase: GetNFTListingViewTypePreferenceUseCase,
    private val getAccountDetailFlow: GetAccountDetailFlow,
    private val getAccountCollectibleDataFlow: GetAccountCollectibleDataFlow,
    isAssetOwnedByAccount: IsAssetOwnedByAccount,
    clearCollectibleFiltersPreferencesUseCase: ClearCollectibleFiltersPreferencesUseCase,
    shouldDisplayOptedInNFTPreferenceUseCase: ShouldDisplayOptedInNFTPreferenceUseCase,
    addOnListingViewTypeChangeListenerUseCase: AddOnListingViewTypeChangeListenerUseCase,
    removeOnListingViewTypeChangeListenerUseCase: RemoveOnListingViewTypeChangeListenerUseCase,
    saveNFTListingViewTypePreferenceUseCase: SaveNFTListingViewTypePreferenceUseCase
) : BaseCollectiblesListingPreviewUseCase(
    collectibleListingItemMapper,
    saveNFTListingViewTypePreferenceUseCase,
    addOnListingViewTypeChangeListenerUseCase,
    removeOnListingViewTypeChangeListenerUseCase,
    shouldDisplayOptedInNFTPreferenceUseCase,
    clearCollectibleFiltersPreferencesUseCase,
    isAssetOwnedByAccount
) {

    fun getCollectiblesListingPreviewFlow(searchKeyword: String, publicKey: String): Flow<CollectiblesListingPreview> {
        return combine(
            getAccountDetailFlow(publicKey),
            failedAssetRepository.getFailedAssetCacheFlow(),
            getAccountCollectibleDataFlow(publicKey),
        ) { accountDetail, failedAssets, accountCollectibleData ->
            val hasAccountAuthority = hasAccountAuthority(accountDetail)
            val nftListingType = getNFTListingViewTypePreferenceUseCase()
            val collectibleListData = prepareCollectiblesListItems(
                searchKeyword = searchKeyword,
                accountDetail = accountDetail,
                accountCollectibleData = accountCollectibleData,
                nftListingType = nftListingType
            )
            val isAllCollectiblesFilteredOut = isAllCollectiblesFilteredOut(collectibleListData, searchKeyword)
            val isEmptyStateVisible = accountCollectibleData.isEmpty() || isAllCollectiblesFilteredOut
            val itemList = mutableListOf<BaseCollectibleListItem>().apply {
                if (!isEmptyStateVisible) {
                    add(createSearchViewItem(query = searchKeyword, nftListingType = nftListingType))
                    add(
                        ACCOUNT_COLLECTIBLES_LIST_CONFIGURATION_HEADER_ITEM_INDEX,
                        createInfoViewItem(
                            displayedCollectibleCount = collectibleListData.displayedCollectibleCount,
                            isAddButtonVisible = hasAccountAuthority
                        )
                    )
                }
                addAll(collectibleListData.baseCollectibleItemList)
            }
            collectibleListingItemMapper.mapToPreviewItem(
                isLoadingVisible = false,
                isEmptyStateVisible = isEmptyStateVisible,
                isErrorVisible = failedAssets.isNotEmpty(),
                itemList = itemList,
                isReceiveButtonVisible = isEmptyStateVisible && hasAccountAuthority,
                filteredCollectibleCount = collectibleListData.filteredOutCollectibleCount,
                isClearFilterButtonVisible = isAllCollectiblesFilteredOut,
                isAccountFabVisible = hasAccountAuthority,
                isAddCollectibleFloatingActionButtonVisible = hasAccountAuthority
            )
        }
    }

    private suspend fun prepareCollectiblesListItems(
        searchKeyword: String,
        accountDetail: AccountDetail?,
        accountCollectibleData: List<BaseAccountAssetData>,
        nftListingType: NFTListingViewType
    ): BaseCollectibleListData {
        var displayedCollectibleCount = 0
        var filteredOutCollectibleCount = 0
        val collectibleList = mutableListOf<BaseCollectibleListItem>().apply {
            accountCollectibleData.filter { nftData ->
                if (accountDetail?.address == null) return@filter false
                filterOptedInNFTIfNeed(accountDetail.address, nftData.id).also { isNotFiltered ->
                    if (!isNotFiltered) filteredOutCollectibleCount++
                }
            }.forEach { collectibleData ->
                filteredOutCollectibleCount++
                if (filterNFTBaseOnSearch(searchKeyword, collectibleData)) return@forEach
                val collectibleListItem = createCollectibleListItem(
                    accountAssetData = collectibleData,
                    optedInAccountAddress = accountDetail?.address.orEmpty(),
                    nftListingType = nftListingType,
                    isOwnedByWatchAccount = accountDetail?.accountType == AccountType.NoAuth
                ) ?: return@forEach
                add(collectibleListItem)
                displayedCollectibleCount++
            }
        }
        val sortedCollectibleItemList = collectibleItemSortUseCase.sortCollectibles(collectibleList)
        return collectibleListingItemMapper.mapToBaseCollectibleListData(
            sortedCollectibleItemList,
            displayedCollectibleCount,
            filteredOutCollectibleCount
        )
    }

    private fun hasAccountAuthority(accountDetail: AccountDetail?): Boolean {
        return accountDetail?.accountType?.canSignTransaction() ?: false
    }

    companion object {
        const val ACCOUNT_COLLECTIBLES_LIST_CONFIGURATION_HEADER_ITEM_INDEX = 0
    }
}
