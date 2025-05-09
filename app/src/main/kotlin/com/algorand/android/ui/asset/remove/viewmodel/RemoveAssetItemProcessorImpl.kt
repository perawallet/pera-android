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

package com.algorand.android.ui.asset.remove.viewmodel

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.algorand.android.R
import com.algorand.android.mapper.RemoveAssetItemMapper
import com.algorand.android.models.ScreenState.CustomState
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.ScreenStateItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetItemProcessorData
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.ExcludedAssetIds
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.SearchKeyword
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RemoveAssetItemProcessorImpl @Inject constructor(
    private val getAssetCollectibleLitesFlow: GetAssetCollectibleLitesFlow,
    private val removeAssetItemMapper: RemoveAssetItemMapper,
) : RemoveAssetItemProcessor {

    override fun getPagedAssetItems(data: RemoveAssetItemProcessorData): Flow<PagingData<BaseRemoveAssetItem>> {
        val query = getAssetCollectibleQuery(data)
        return getAssetCollectibleLitesFlow(query).map { pagingData ->
            pagingData.map { assetLite ->
                removeAssetItemMapper.mapToRemoveAssetItem(assetLite)
            }.insertSeparators { removeAssetItem: BaseRemoveAssetItem?, removeAssetItem2: BaseRemoveAssetItem? ->
                val isResultEmpty = removeAssetItem == null && removeAssetItem2 == null
                if (isResultEmpty) {
                    getScreenStateOrNull(data)
                } else {
                    null
                }
            }
        }
    }

    private fun getScreenStateOrNull(data: RemoveAssetItemProcessorData): ScreenStateItem? {
        return with(data) {
            when {
                searchKeyword.isBlank() && !isThereAnyAssetCanAddressOptOut -> {
                    ScreenStateItem(CustomState(title = R.string.we_couldn_t_find_any_assets))
                }
                searchKeyword.isNotBlank() && isThereAnyAssetCanAddressOptOut -> {
                    ScreenStateItem(CustomState(title = R.string.no_asset_found))
                }
                else -> null
            }
        }
    }

    private fun getAssetCollectibleQuery(data: RemoveAssetItemProcessorData): AssetCollectibleLiteQuery {
        val filters = mutableListOf<AssetCollectibleLiteQueryFilter>().apply {
            if (data.searchKeyword.isNotBlank()) add(SearchKeyword(data.searchKeyword))
            add(ExcludedAssetIds(ALGO_ID))
        }
        return AssetCollectibleLiteQuery(data.address, data.sortType, filters)
    }
}
