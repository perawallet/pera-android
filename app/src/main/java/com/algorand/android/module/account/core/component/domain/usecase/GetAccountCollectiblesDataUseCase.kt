/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetCollectibleDetail
import com.algorand.android.module.account.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import javax.inject.Inject

internal class GetAccountCollectiblesDataUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getCollectibleDetail: GetCollectibleDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectiblesData {

    override suspend fun invoke(address: String): List<BaseOwnedCollectibleData> {
        val accountInformation = getAccountInformation(address) ?: return emptyList()
        return getAccountCollectibleListData(accountInformation)
    }

    override suspend fun invoke(accountInformation: AccountInformation): List<BaseOwnedCollectibleData> {
        return getAccountCollectibleListData(accountInformation)
    }

    private suspend fun getAccountCollectibleListData(
        accountInformation: AccountInformation
    ): MutableList<BaseOwnedCollectibleData> {
        val accountAssetDataList = mutableListOf<BaseOwnedCollectibleData>()

        accountInformation.assetHoldings.forEach { assetHolding ->
            getCollectibleDetail(assetHolding.assetId)?.run {
                val collectibleData = baseOwnedCollectibleDataFactory(assetHolding, this)
                accountAssetDataList.add(collectibleData)
            }
        }
        return accountAssetDataList
    }
}
