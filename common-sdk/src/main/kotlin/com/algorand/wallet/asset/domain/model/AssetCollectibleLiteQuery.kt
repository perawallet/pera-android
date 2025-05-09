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

package com.algorand.wallet.asset.domain.model

data class AssetCollectibleLiteQuery(
    val addresses: List<String>,
    val sortType: AssetCollectibleLiteSortType,
    val filters: List<AssetCollectibleLiteQueryFilter> = emptyList()
) {

    constructor(
        address: String,
        sortType: AssetCollectibleLiteSortType,
        filters: List<AssetCollectibleLiteQueryFilter> = emptyList()
    ) : this(addresses = listOf(address), sortType = sortType, filters = filters)

    fun getExcludedAssetIds(): List<Long> {
        return filters.filterIsInstance<AssetCollectibleLiteQueryFilter.ExcludedAssetIds>()
            .flatMap { it.excludedAssetIds }
            .distinct()
    }

    fun getSearchKeyword(): String? {
        return filters.filterIsInstance<AssetCollectibleLiteQueryFilter.SearchKeyword>()
            .firstOrNull()
            ?.keyword
    }
}
