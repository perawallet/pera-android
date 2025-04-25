/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAccountCollectibleDataFlowUseCase @Inject constructor(
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getCollectibleDetail: GetCollectibleDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectibleDataFlow {

    override fun invoke(address: String): Flow<List<BaseOwnedCollectibleData>> {
        return getAccountInformationFlow(address).map {
            val accountInformation = it ?: return@map emptyList()
            val accountAssetDataList = mutableListOf<BaseOwnedCollectibleData>()
            accountInformation.assetHoldings.forEach { assetHolding ->
                val collectibleDetail = getCollectibleDetail(assetHolding.assetId)
                if (collectibleDetail != null) {
                    val collectibleData = baseOwnedCollectibleDataFactory(assetHolding, collectibleDetail)
                    accountAssetDataList.add(collectibleData)
                }
            }
            accountAssetDataList
        }
    }
}
