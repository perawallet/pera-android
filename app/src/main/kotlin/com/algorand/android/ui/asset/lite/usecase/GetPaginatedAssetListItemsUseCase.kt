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

package com.algorand.android.ui.asset.lite.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.asset.lite.mapper.AssetListItemBalanceMapper
import com.algorand.android.ui.asset.lite.model.AssetListItemBalancePayload
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPaginatedAssetListItemsUseCase @Inject constructor(
    private val getAssetCollectibleLitesFlow: GetAssetCollectibleLitesFlow,
    private val verificationTierMapper: VerificationTierConfigurationDecider,
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val assetListItemBalanceMapper: AssetListItemBalanceMapper
) : GetPaginatedAssetListItems {

    override fun invoke(query: AssetCollectibleLiteQuery): Flow<PagingData<AssetListItem>> {
        return getAssetCollectibleLitesFlow(query).map { pagingData ->
            pagingData.map { assetLite ->
                assetLite.toAssetListItem()
            }
        }
    }

    private fun AssetLite.toAssetListItem(): AssetListItem {
        val balanceMapperPayload = AssetListItemBalancePayload(assetId, amount, decimal, usdValue, type)
        return AssetListItem(
            assetId = this.assetId,
            name = this.name,
            unitName = this.shortName,
            balance = assetListItemBalanceMapper(balanceMapperPayload),
            verificationTier = verificationTierMapper.decideVerificationTierConfiguration(verificationTier),
            assetIcon = assetIconDrawableMapper.map(this)
        )
    }
}
