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

package com.algorand.android.modules.assets.remove.ui.usecase

import com.algorand.android.R
import com.algorand.android.mapper.RemoveAssetItemMapper
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.models.BaseRemoveAssetItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem
import com.algorand.android.models.ScreenState
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountCollectibleDataFlow
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountOwnedAssetsDataFlow
import com.algorand.android.modules.assets.remove.ui.mapper.RemoveAssetsPreviewMapper
import com.algorand.android.modules.assets.remove.ui.model.RemoveAssetsPreview
import com.algorand.android.modules.sorting.assetsorting.ui.usecase.AssetItemSortUseCase
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingsFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RemoveAssetsPreviewUseCase @Inject constructor(
    private val removeAssetsPreviewMapper: RemoveAssetsPreviewMapper,
    private val assetItemSortUseCase: AssetItemSortUseCase,
    private val removeAssetItemMapper: RemoveAssetItemMapper,
    private val getAccountOwnedAssetsDataFlow: GetAccountOwnedAssetsDataFlow,
    private val getAccountOwnedCollectibleDataFlow: GetAccountCollectibleDataFlow,
    private val getAccountAssetHoldingsFlow: GetAccountAssetHoldingsFlow
) {

    fun initRemoveAssetsPreview(accountAddress: String, query: String): Flow<RemoveAssetsPreview?> {
        return combine(
            getAccountOwnedAssetsDataFlow(accountAddress, false),
            getAccountOwnedCollectibleDataFlow(accountAddress),
            getAccountAssetHoldingsFlow(accountAddress)
        ) { accountOwnedAssets, accountOwnedCollectibles, assetHoldingList ->
            val sortedRemovableListItems = mutableListOf<BaseRemovableItem>().apply {
                addAll(
                    createBaseRemoveAssetItems(
                        accountOwnedAssets = accountOwnedAssets,
                        query = query,
                        accountAddress = accountAddress,
                        assetHoldingList = assetHoldingList
                    )
                )
                addAll(
                    createBaseRemoveCollectibleItems(
                        accountOwnedCollectibles = accountOwnedCollectibles,
                        query = query,
                        accountAddress = accountAddress,
                        assetHoldingList = assetHoldingList
                    )
                )
            }.run { assetItemSortUseCase.sortAssets(this) }

            val removableAssetList = mutableListOf<BaseRemoveAssetItem>().apply {
                add(removeAssetItemMapper.mapToTitleItem(R.string.asset_opt_out))
                add(removeAssetItemMapper.mapToDescriptionItem(R.string.to_opt_out_from_an_asset))
                if (shouldAddSearchView(sortedRemovableListItems, query)) {
                    add(removeAssetItemMapper.mapToSearchItem(R.string.search_my_assets))
                }
                addAll(sortedRemovableListItems)
                getScreenStateOrNull(sortedRemovableListItems, query)?.let {
                    add(removeAssetItemMapper.mapToScreenStateItem(it))
                }
            }

            removeAssetsPreviewMapper.mapToRemoveAssetsPreview(removableAssetList = removableAssetList)
        }
    }

    private fun createBaseRemoveAssetItems(
        accountOwnedAssets: List<OwnedAssetData>,
        query: String,
        accountAddress: String,
        assetHoldingList: List<AssetHolding>?
    ): List<BaseRemovableItem> {
        return accountOwnedAssets.mapNotNull { ownedAssetData ->
            val assetHolding = assetHoldingList?.firstOrNull { it.assetId == ownedAssetData.id }
            if (ownedAssetData.name?.contains(query, true) == true &&
                ownedAssetData.creatorPublicKey != accountAddress
            ) {
                removeAssetItemMapper.mapToRemoveAssetItem(
                    ownedAssetData = ownedAssetData,
                    assetHoldingStatus = assetHolding?.status
                )
            } else {
                null
            }
        }
    }

    private fun createBaseRemoveCollectibleItems(
        accountOwnedCollectibles: List<BaseOwnedCollectibleData>,
        query: String,
        accountAddress: String,
        assetHoldingList: List<AssetHolding>?
    ): List<BaseRemovableItem> {
        return accountOwnedCollectibles.mapNotNull { ownedCollectible ->
            val assetHoldingStatus = assetHoldingList?.firstOrNull { it.assetId == ownedCollectible.id }?.status
            if (ownedCollectible.name?.contains(query, true) == true &&
                ownedCollectible.creatorPublicKey != accountAddress
            ) {
                removeAssetItemMapper.mapToRemovableItem(ownedCollectible, assetHoldingStatus)
            } else {
                null
            }
        }
    }

    private fun getScreenStateOrNull(
        removableListItems: List<BaseRemovableItem>,
        query: String
    ): ScreenState.CustomState? {
        return when {
            query.isBlank() && removableListItems.isEmpty() -> {
                ScreenState.CustomState(title = R.string.we_couldn_t_find_any_assets)
            }
            query.isNotBlank() && removableListItems.isEmpty() -> {
                ScreenState.CustomState(title = R.string.no_asset_found)
            }
            else -> null
        }
    }

    private fun shouldAddSearchView(removableListItems: List<BaseRemovableItem>, query: String): Boolean {
        return (query.isBlank() && removableListItems.isEmpty()).not()
    }
}
