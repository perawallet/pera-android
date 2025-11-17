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

package com.algorand.android.assetsearch.domain.pagination

import androidx.paging.PagingData
import com.algorand.android.assetsearch.domain.model.AssetSearchDTO
import com.algorand.android.assetsearch.domain.model.AssetSearchQuery
import com.algorand.android.assetsearch.domain.repository.AssetSearchRepository
import com.algorand.wallet.asset.domain.usecase.GetAssetFavoriteStatuses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AssetSearchPagination @Inject constructor() {

    private val querySharedFlow = MutableSharedFlow<AssetSearchQuery>()
    private lateinit var assetSearchPager: AssetSearchPager

    fun initPagination(
        assetSearchPagerBuilder: AssetSearchPagerBuilder,
        scope: CoroutineScope,
        repository: AssetSearchRepository,
        defaultQuery: AssetSearchQuery,
        getAssetFavoriteStatuses: GetAssetFavoriteStatuses,
        queryDebounce: Long = DEFAULT_QUERY_DEBOUNCE
    ): Flow<PagingData<AssetSearchDTO>> {
        assetSearchPager = assetSearchPagerBuilder.build(repository, getAssetFavoriteStatuses, defaultQuery)
        querySharedFlow
            .debounce(queryDebounce)
            .onEach { assetSearchPager.updateQuery(it) }
            .flowOn(Dispatchers.Default)
            .launchIn(scope)
        return assetSearchPager.toFlow(scope)
    }

    suspend fun searchAsset(query: AssetSearchQuery) {
        querySharedFlow.emit(query)
    }

    fun invalidateDataSource() {
        assetSearchPager.invalidateDataSource()
    }

    companion object {
        private const val DEFAULT_QUERY_DEBOUNCE = 400L
    }
}
