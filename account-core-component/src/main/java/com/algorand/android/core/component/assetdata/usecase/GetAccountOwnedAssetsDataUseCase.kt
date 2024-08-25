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

package com.algorand.android.core.component.assetdata.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.model.AssetHolding
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import javax.inject.Inject

internal class GetAccountOwnedAssetsDataUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val createAccountOwnedAssetData: CreateAccountOwnedAssetData,
    private val createAlgoOwnedAssetData: CreateAlgoOwnedAssetData,
    private val getAssetDetail: GetAssetDetail
) : GetAccountOwnedAssetsData {

    override suspend fun invoke(address: String, includeAlgo: Boolean): List<OwnedAssetData> {
        val accountInformation = getAccountInformation(address) ?: return emptyList()
        return getAssetDataList(accountInformation, includeAlgo)
    }

    override suspend fun invoke(accountInformation: AccountInformation, includeAlgo: Boolean): List<OwnedAssetData> {
        return getAssetDataList(accountInformation, includeAlgo)
    }

    private suspend fun getAssetDataList(accountInfo: AccountInformation, includeAlgo: Boolean): List<OwnedAssetData> {
        return mutableListOf<OwnedAssetData>().apply {
            if (includeAlgo) add(createAlgoOwnedAssetData(accountInfo.amount))
            addAll(getOwnedAssetDataList(accountInfo))
        }
    }

    private suspend fun getOwnedAssetDataList(accountInformation: AccountInformation): List<OwnedAssetData> {
        return accountInformation.assetHoldings.mapNotNull { assetHolding ->
            getOwnedAssetData(assetHolding)
        }
    }

    private suspend fun getOwnedAssetData(assetHolding: AssetHolding): OwnedAssetData? {
        return getAssetDetail(assetHolding.assetId)?.let { assetDetail ->
            createAccountOwnedAssetData(assetDetail, assetHolding)
        }
    }
}
