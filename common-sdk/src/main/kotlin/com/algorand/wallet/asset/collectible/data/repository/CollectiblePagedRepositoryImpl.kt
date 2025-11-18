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

package com.algorand.wallet.asset.collectible.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.algorand.wallet.asset.collectible.data.database.dao.PaginatedCollectibleDao
import com.algorand.wallet.asset.collectible.data.mapper.model.CollectibleLiteSortTypeQueryMapper
import com.algorand.wallet.asset.collectible.domain.model.CollectibleLiteQuery
import com.algorand.wallet.asset.collectible.domain.model.CollectibleLiteQueryFilter.FilterOutZeroAmount
import com.algorand.wallet.asset.collectible.domain.model.FilteredCollectibleCount
import com.algorand.wallet.asset.collectible.domain.repository.CollectiblePagedRepository
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.data.mapper.model.AssetLiteMapper
import com.algorand.wallet.asset.domain.model.AssetLite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CollectiblePagedRepositoryImpl @Inject constructor(
    private val paginatedCollectibleDao: PaginatedCollectibleDao,
    private val assetLiteMapper: AssetLiteMapper,
    private val collectibleLiteSortTypeQueryMapper: CollectibleLiteSortTypeQueryMapper
) : CollectiblePagedRepository {

    override fun getCollectibleLiteCountFlow(query: CollectibleLiteQuery): Flow<FilteredCollectibleCount> {
        return paginatedCollectibleDao.getFilteredCollectibleCount(
            addressList = query.addresses,
            searchKeyword = query.getSearchKeyword(),
            filterOutZeroAmount = query.filters.contains(FilterOutZeroAmount)
        ).map {
            FilteredCollectibleCount(it.totalCount, it.filteredAndSearchQueriedCount, it.totalFilteredOutCount)
        }
    }

    override fun getPaginatedAssetCollectibleLiteItems(query: CollectibleLiteQuery): Flow<PagingData<AssetLite>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { getPagingSource(query) }
        ).flow.map { pagingData ->
            pagingData.map { pagedItem ->
                assetLiteMapper(pagedItem)
            }
        }
    }

    private fun getPagingSource(query: CollectibleLiteQuery): PagingSource<Int, PaginatedAssetCollectibleItemDto> {
        return paginatedCollectibleDao.getPaginatedAssetCollectibleItems(
            addressList = query.addresses,
            searchKeyword = query.getSearchKeyword(),
            filterOutZeroAmount = query.filters.contains(FilterOutZeroAmount),
            sortType = collectibleLiteSortTypeQueryMapper(query.sortType)
        )
    }

    private companion object {
        const val PAGE_SIZE = 50
    }
}
