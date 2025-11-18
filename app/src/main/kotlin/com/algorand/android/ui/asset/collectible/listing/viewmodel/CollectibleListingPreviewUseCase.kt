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

package com.algorand.android.ui.asset.collectible.listing.viewmodel

import androidx.paging.PagingData
import com.algorand.android.modules.collectibles.filter.domain.usecase.ShouldDisplayOptedInNFTPreferenceUseCase
import com.algorand.android.modules.sorting.nftsorting.domain.model.CollectibleSortPreference
import com.algorand.android.modules.sorting.nftsorting.domain.usecase.CollectibleSortTypeUseCase
import com.algorand.wallet.asset.collectible.domain.model.CollectibleLiteQuery
import com.algorand.wallet.asset.collectible.domain.model.CollectibleLiteQueryFilter
import com.algorand.wallet.asset.collectible.domain.model.CollectibleLiteSortType
import com.algorand.wallet.asset.collectible.domain.model.FilteredCollectibleCount
import com.algorand.wallet.asset.collectible.domain.usecase.GetCollectibleLiteCountFlow
import com.algorand.wallet.asset.collectible.domain.usecase.GetCollectibleLitesFlow
import com.algorand.wallet.asset.domain.model.AssetLite
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CollectibleListingPreviewUseCase @Inject constructor(
    private val getCollectibleLitesFlow: GetCollectibleLitesFlow,
    private val shouldDisplayOptedInNFT: ShouldDisplayOptedInNFTPreferenceUseCase,
    private val getCollectibleLiteCountFlow: GetCollectibleLiteCountFlow,
    private val collectibleSortTypeUseCase: CollectibleSortTypeUseCase
) {

    suspend fun getCollectibleLiteCountFlow(
        searchKeyword: String?,
        addresses: List<String>
    ): Flow<FilteredCollectibleCount> {
        val query = getCollectibleLiteQuery(searchKeyword, addresses)
        return getCollectibleLiteCountFlow(query)
    }

    suspend fun getCollectibleItemsFlow(searchKeyword: String?, addresses: List<String>): Flow<PagingData<AssetLite>> {
        val query = getCollectibleLiteQuery(searchKeyword, addresses)
        return getCollectibleLitesFlow(query)
    }

    private suspend fun getCollectibleLiteQuery(searchKeyword: String?, addresses: List<String>): CollectibleLiteQuery {
        return CollectibleLiteQuery(
            addresses = addresses,
            sortType = getCollectibleLiteSortType(),
            filters = mutableListOf<CollectibleLiteQueryFilter>().apply {
                searchKeyword?.let {
                    add(CollectibleLiteQueryFilter.SearchKeyword(it))
                }
                if (!shouldDisplayOptedInNFT()) {
                    add(CollectibleLiteQueryFilter.FilterOutZeroAmount)
                }
            }
        )
    }

    private suspend fun getCollectibleLiteSortType(): CollectibleLiteSortType {
        return when (collectibleSortTypeUseCase.getSortPreferenceType()) {
            CollectibleSortPreference.ALPHABETICALLY_ASCENDING -> CollectibleLiteSortType.NameAscending
            CollectibleSortPreference.ALPHABETICALLY_DESCENDING -> CollectibleLiteSortType.NameDescending
            CollectibleSortPreference.NEWEST_TO_OLDEST -> CollectibleLiteSortType.OptInDescending
            CollectibleSortPreference.OLDEST_TO_NEWEST -> CollectibleLiteSortType.OptInAscending
        }
    }
}
