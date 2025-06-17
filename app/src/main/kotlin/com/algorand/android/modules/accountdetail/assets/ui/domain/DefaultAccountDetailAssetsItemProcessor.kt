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

package com.algorand.android.modules.accountdetail.assets.ui.domain

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.algorand.android.modules.accountdetail.assets.ui.mapper.AccountDetailAssetItemMapper
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem
import com.algorand.android.modules.accountsorting.domain.usecase.GetAssetCollectibleLiteSortType
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldDisplayNFTInAssetsPreferenceUseCase
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldDisplayOptedInNFTInAssetsPreferenceUseCase
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldHideZeroBalanceAssetsPreferenceUseCase
import com.algorand.android.ui.common.amount.mapper.AmountRendererTypeMapper
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutCollectibles
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutCollectiblesWithZeroAmount
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutZeroAmount
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.SearchKeyword
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import com.algorand.wallet.privacy.domain.usecase.GetPrivacyModeFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class DefaultAccountDetailAssetsItemProcessor @Inject constructor(
    private val accountDetailAssetItemMapper: AccountDetailAssetItemMapper,
    private val getAssetCollectibleLitesFlow: GetAssetCollectibleLitesFlow,
    private val shouldHideZeroBalanceAssetsPreferenceUseCase: ShouldHideZeroBalanceAssetsPreferenceUseCase,
    private val shouldDisplayNFTInAssetsPreferenceUseCase: ShouldDisplayNFTInAssetsPreferenceUseCase,
    private val shouldDisplayOptedInNFTInAssetsPreferenceUseCase: ShouldDisplayOptedInNFTInAssetsPreferenceUseCase,
    private val getAssetCollectibleLiteSortType: GetAssetCollectibleLiteSortType,
    private val getPrivacyModeFlow: GetPrivacyModeFlow,
    private val amountRendererTypeMapper: AmountRendererTypeMapper
) : AccountDetailAssetsItemProcessor {

    override suspend fun getAssetsPagingFlow(
        scope: CoroutineScope,
        address: String,
        query: String
    ): Flow<PagingData<AccountDetailAssetsItem>> {
        val assetCollectibleLiteQuery = getPaginationQuery(address, query)
        return combine(
            getAssetCollectibleLitesFlow(assetCollectibleLiteQuery).cachedIn(scope),
            getPrivacyModeFlow()
        ) { pagingData, privacyMode ->
            pagingData.map { assetLite ->
                accountDetailAssetItemMapper.mapToAssetListItem(assetLite, false, amountRendererTypeMapper(privacyMode))
            }.insertSeparators { assetItem1: AccountDetailAssetsItem?, assetItem2: AccountDetailAssetsItem? ->
                if (assetItem1 == null && assetItem2 == null) {
                    accountDetailAssetItemMapper.mapToNoAssetFoundViewItem()
                } else {
                    null
                }
            }
        }.distinctUntilChanged()
    }

    private suspend fun getPaginationQuery(address: String, searchKeyword: String): AssetCollectibleLiteQuery {
        val filters = mutableListOf<AssetCollectibleLiteQueryFilter>().apply {
            if (shouldHideZeroBalanceAssetsPreferenceUseCase()) add(FilterOutZeroAmount)
            if (!shouldDisplayNFTInAssetsPreferenceUseCase()) add(FilterOutCollectibles)
            if (!shouldDisplayOptedInNFTInAssetsPreferenceUseCase()) add(FilterOutCollectiblesWithZeroAmount)
            if (searchKeyword.isNotBlank()) add(SearchKeyword(searchKeyword))
        }
        return AssetCollectibleLiteQuery(
            addresses = listOf(address),
            sortType = getAssetCollectibleLiteSortType(),
            filters = filters
        )
    }
}
