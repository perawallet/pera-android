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

package com.algorand.android.nft.domain.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldings
import com.algorand.wallet.asset.domain.usecase.GetCollectiblesDetail
import javax.inject.Inject

internal class GetAccountCollectiblesDataUseCase @Inject constructor(
    private val getAccountAssetHoldings: GetAccountAssetHoldings,
    private val getCollectiblesDetail: GetCollectiblesDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectiblesData {

    override suspend fun invoke(address: String): List<BaseOwnedCollectibleData> {
        val accountAssetHoldings = getAccountAssetHoldings(address)
        return getAccountCollectibleListData(accountAssetHoldings)
    }

    private suspend fun getAccountCollectibleListData(
        assetHoldings: List<AssetHolding>
    ): MutableList<BaseOwnedCollectibleData> {
        val accountAssetDataList = mutableListOf<BaseOwnedCollectibleData>()

        val assetHoldingMap = assetHoldings.associateBy { it.assetId }
        val ownedCollectibleDetails = getCollectiblesDetail(assetHoldingMap.keys.toList())

        ownedCollectibleDetails.forEach { collectibleDetail ->
            assetHoldingMap[collectibleDetail.id]?.let {
                val collectibleData = baseOwnedCollectibleDataFactory(it, collectibleDetail)
                accountAssetDataList.add(collectibleData)
            }
        }
        return accountAssetDataList
    }
}
