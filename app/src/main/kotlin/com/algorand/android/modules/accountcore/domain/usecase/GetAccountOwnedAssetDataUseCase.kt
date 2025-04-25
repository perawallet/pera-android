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

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject

internal class GetAccountOwnedAssetDataUseCase @Inject constructor(
    private val getAssetDetail: GetAssetDetail,
    private val getAccountAlgoBalance: GetAccountAlgoBalance,
    private val getAccountAssetHolding: GetAccountAssetHolding,
    private val createAccountOwnedAssetData: CreateAccountOwnedAssetData,
    private val createAlgoOwnedAssetData: CreateAlgoOwnedAssetData
) : GetAccountOwnedAssetData {

    override suspend fun invoke(address: String, assetId: Long): OwnedAssetData? {
        return if (assetId == ALGO_ID) {
            createOwnedAlgo(address)
        } else {
            createOwnedAsset(address, assetId)
        }
    }

    private suspend fun createOwnedAsset(address: String, assetId: Long): OwnedAssetData? {
        val assetHolding = getAccountAssetHolding(address, assetId) ?: return null
        val assetDetail = getAssetDetail(assetId) ?: return null
        return createAccountOwnedAssetData(assetDetail, assetHolding)
    }

    private suspend fun createOwnedAlgo(address: String): OwnedAssetData? {
        val algoAmount = getAccountAlgoBalance(address) ?: return null
        return createAlgoOwnedAssetData(algoAmount)
    }
}
