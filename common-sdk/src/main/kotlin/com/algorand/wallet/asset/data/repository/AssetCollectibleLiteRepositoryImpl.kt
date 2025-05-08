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

package com.algorand.wallet.asset.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.algorand.wallet.asset.data.database.dao.PaginatedAssetCollectibleDao
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.data.mapper.model.AssetCollectibleLiteSortTypeQueryMapper
import com.algorand.wallet.asset.data.mapper.model.AssetLiteMapper
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutCollectibles
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutCollectiblesWithZeroAmount
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutZeroAmount
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.repository.AssetCollectibleLiteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AssetCollectibleLiteRepositoryImpl @Inject constructor(
    private val assetCollectibleLiteSortTypeQueryMapper: AssetCollectibleLiteSortTypeQueryMapper,
    private val assetLiteMapper: AssetLiteMapper,
    private val paginatedAssetCollectibleDao: PaginatedAssetCollectibleDao
) : AssetCollectibleLiteRepository {

    override fun getPaginatedAssetCollectibleLiteItems(query: AssetCollectibleLiteQuery): Flow<PagingData<AssetLite>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { getPagingSource(query) }
        ).flow.map { pagingData ->
            pagingData.map { pagedItem ->
                assetLiteMapper(pagedItem)
            }
        }
    }

    private fun getPagingSource(query: AssetCollectibleLiteQuery): PagingSource<Int, PaginatedAssetCollectibleItemDto> {
        return paginatedAssetCollectibleDao.getPaginatedAssetCollectibleItems(
            addressList = query.addresses,
            searchKeyword = query.getSearchKeyword(),
            filterOutZeroAmount = query.filters.contains(FilterOutZeroAmount),
            filterOutCollectibles = query.filters.contains(FilterOutCollectibles),
            filterOutCollectiblesWithZeroAmount = query.filters.contains(FilterOutCollectiblesWithZeroAmount),
            sortType = assetCollectibleLiteSortTypeQueryMapper(query.sortType),
            excludedAssetIds = query.getExcludedAssetIds()
        )
    }

    private companion object {
        const val PAGE_SIZE = 50
    }
}
