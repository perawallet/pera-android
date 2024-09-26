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

package com.algorand.android.module.account.core.component.assetdata.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import javax.inject.Inject

internal class GetAccountOwnedAssetDataUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getAssetDetail: GetAssetDetail,
    private val createAccountOwnedAssetData: CreateAccountOwnedAssetData,
    private val createAlgoOwnedAssetData: CreateAlgoOwnedAssetData
) : GetAccountOwnedAssetData {

    override suspend fun invoke(address: String, assetId: Long, includeAlgo: Boolean): OwnedAssetData? {
        val accountInfo = getAccountInformation(address) ?: return null
        return if (includeAlgo && assetId == ALGO_ASSET_ID) {
            createOwnedAlgo(accountInfo)
        } else {
            createOwnedAsset(accountInfo, assetId)
        }
    }

    private suspend fun createOwnedAsset(accountInformation: AccountInformation, assetId: Long): OwnedAssetData? {
        val assetHolding = accountInformation.assetHoldings.find { it.assetId == assetId } ?: return null
        val assetDetail = getAssetDetail(assetId) ?: return null
        return createAccountOwnedAssetData(assetDetail, assetHolding)
    }

    private suspend fun createOwnedAlgo(accountInformation: AccountInformation): OwnedAssetData {
        val algoAmount = accountInformation.amount
        return createAlgoOwnedAssetData(algoAmount)
    }
}
