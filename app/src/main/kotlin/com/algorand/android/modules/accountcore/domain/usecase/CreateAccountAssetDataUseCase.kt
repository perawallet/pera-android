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

import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.accountcore.domain.model.AccountAssetData
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.asset.lite.domain.usecase.GetAssetDetailsLite
import java.math.BigInteger
import javax.inject.Inject

internal class CreateAccountAssetDataUseCase @Inject constructor(
    private val getAssetDetailsLite: GetAssetDetailsLite,
    private val createAlgoOwnedAssetData: CreateAlgoOwnedAssetData,
    private val createAccountOwnedAssetData: CreateAccountOwnedAssetData,
    private val createAccountPendingAdditionAssetData: CreateAccountPendingAdditionAssetData,
    private val createAccountPendingDeletionAssetData: CreateAccountPendingDeletionAssetData,
    private val getAccountAlgoBalance: GetAccountAlgoBalance
) : CreateAccountAssetData {

    override suspend fun invoke(
        address: String,
        assetHoldings: List<AssetHolding>,
        includeAlgo: Boolean
    ): AccountAssetData {
        val ownedAssetDataList = mutableListOf<BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData>()
        val pendingAdditionAssetDataList = mutableListOf<BaseAccountAssetData.PendingAssetData.AdditionAssetData>()
        val pendingDeletionAssetDataList = mutableListOf<BaseAccountAssetData.PendingAssetData.DeletionAssetData>()

        if (includeAlgo) {
            val algoBalance = getAccountAlgoBalance(address) ?: BigInteger.ZERO
            ownedAssetDataList.add(createAlgoOwnedAssetData(algoBalance))
        }

        val assetHoldingsMap = assetHoldings.associateBy { it.assetId }
        val ownedAssetDetails = getAssetDetailsLite(assetHoldingsMap.keys.toList())
        ownedAssetDetails.values.filterNotNull().forEach { assetDetailLite ->
            assetHoldingsMap[assetDetailLite.id]?.let { assetHolding ->
                when (assetHolding.status) {
                    AssetStatus.PENDING_FOR_REMOVAL -> {
                        val assetData = createAccountPendingDeletionAssetData(assetDetailLite)
                        pendingDeletionAssetDataList.add(assetData)
                    }

                    AssetStatus.PENDING_FOR_ADDITION -> {
                        val assetData = createAccountPendingAdditionAssetData(assetDetailLite)
                        pendingAdditionAssetDataList.add(assetData)
                    }

                    AssetStatus.OWNED_BY_ACCOUNT -> {
                        val assetData = createAccountOwnedAssetData(assetDetailLite, assetHolding)
                        ownedAssetDataList.add(assetData)
                    }
                }
            }
        }

        return AccountAssetData(
            ownedAssetDataList,
            pendingAdditionAssetDataList,
            pendingDeletionAssetDataList
        )
    }
}
