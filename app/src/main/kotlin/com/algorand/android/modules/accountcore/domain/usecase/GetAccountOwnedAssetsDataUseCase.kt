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

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldings
import com.algorand.wallet.asset.domain.usecase.GetAssetDetails
import java.math.BigInteger
import javax.inject.Inject

internal class GetAccountOwnedAssetsDataUseCase @Inject constructor(
    private val createAccountOwnedAssetData: CreateAccountOwnedAssetData,
    private val createAlgoOwnedAssetData: CreateAlgoOwnedAssetData,
    private val getAccountAlgoBalance: GetAccountAlgoBalance,
    private val getAccountAssetHoldings: GetAccountAssetHoldings,
    private val getAssetDetails: GetAssetDetails
) : GetAccountOwnedAssetsData {

    override suspend fun invoke(address: String, includeAlgo: Boolean): List<OwnedAssetData> {
        val assetHoldings = getAccountAssetHoldings(address)
        return getAssetDataList(address, assetHoldings, includeAlgo)
    }

    override suspend fun invoke(
        address: String,
        assetHoldings: List<AssetHolding>,
        includeAlgo: Boolean
    ): List<OwnedAssetData> {
        return getAssetDataList(address, assetHoldings, includeAlgo)
    }

    private suspend fun getAssetDataList(
        address: String,
        assetHoldings: List<AssetHolding>,
        includeAlgo: Boolean
    ): List<OwnedAssetData> {
        return mutableListOf<OwnedAssetData>().apply {
            if (includeAlgo) {
                val algoBalance = getAccountAlgoBalance(address) ?: BigInteger.ZERO
                add(createAlgoOwnedAssetData(algoBalance))
            }
            addAll(getOwnedAssetDataList(assetHoldings))
        }
    }

    private suspend fun getOwnedAssetDataList(assetHoldings: List<AssetHolding>): List<OwnedAssetData> {
        val assetHoldingsMap = assetHoldings.associateBy { it.assetId }
        val assetDetails = getAssetDetails(assetHoldingsMap.keys.toList()).associateBy { it.id }
        return assetHoldingsMap.mapNotNull { (id, assetHolding) ->
            val assetDetail = assetDetails[id] ?: return@mapNotNull null
            createAccountOwnedAssetData(assetDetail, assetHolding)
        }
    }
}
