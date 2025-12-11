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

package com.algorand.android.assetsearch.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.algorand.android.assetsearch.domain.mapper.SearchedAssetMapper
import com.algorand.android.assetsearch.domain.model.AssetSearchDTO
import com.algorand.android.assetsearch.domain.model.AssetSearchQuery
import com.algorand.android.assetsearch.domain.model.BaseSearchedAsset
import com.algorand.android.assetsearch.domain.pagination.AssetSearchPagerBuilder
import com.algorand.android.assetsearch.domain.pagination.AssetSearchPagination
import com.algorand.android.assetsearch.domain.repository.AssetSearchRepository
import com.algorand.android.assetsearch.domain.repository.AssetSearchRepository.Companion.REPOSITORY_INJECTION_NAME
import com.algorand.wallet.asset.domain.usecase.GetAssetFavoriteStatuses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class SearchAssetUseCase @Inject constructor(
    @param:Named(REPOSITORY_INJECTION_NAME) private val assetSearchRepository: AssetSearchRepository,
    private val assetSearchPagination: AssetSearchPagination,
    private val searchedAssetMapper: SearchedAssetMapper,
    private val getAssetFavoriteStatuses: GetAssetFavoriteStatuses
) {

    fun createPaginationFlow(
        builder: AssetSearchPagerBuilder,
        scope: CoroutineScope,
        defaultQuery: AssetSearchQuery
    ): Flow<PagingData<BaseSearchedAsset>> {
        return assetSearchPagination
            .initPagination(builder, scope, assetSearchRepository, defaultQuery, getAssetFavoriteStatuses)
            .map { pagingData -> pagingData.map { createBaseSearchedAsset(it) } }
            .distinctUntilChanged()
    }

    suspend fun searchAsset(query: AssetSearchQuery) {
        assetSearchPagination.searchAsset(query)
    }

    fun invalidateDataSource() {
        assetSearchPagination.invalidateDataSource()
    }

    private fun createBaseSearchedAsset(assetSearchDTO: AssetSearchDTO): BaseSearchedAsset {
        return if (assetSearchDTO.collectible != null) {
            searchedAssetMapper.mapToSearchedCollectible(assetSearchDTO)
        } else {
            searchedAssetMapper.mapToSearchedAsset(assetSearchDTO)
        }
    }
}
