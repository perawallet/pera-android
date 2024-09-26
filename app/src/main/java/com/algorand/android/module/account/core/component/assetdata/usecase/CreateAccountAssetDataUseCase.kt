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

import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.model.AssetStatus.OWNED_BY_ACCOUNT
import com.algorand.android.module.account.info.domain.model.AssetStatus.PENDING_FOR_ADDITION
import com.algorand.android.module.account.info.domain.model.AssetStatus.PENDING_FOR_REMOVAL
import com.algorand.android.module.account.info.domain.model.AssetStatus.PENDING_FOR_SENDING
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.module.account.core.component.assetdata.model.AccountAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.PendingAssetData
import javax.inject.Inject

internal class CreateAccountAssetDataUseCase @Inject constructor(
    private val getAssetDetail: GetAssetDetail,
    private val createAlgoOwnedAssetData: CreateAlgoOwnedAssetData,
    private val createAccountOwnedAssetData: CreateAccountOwnedAssetData,
    private val createAccountPendingAdditionAssetData: CreateAccountPendingAdditionAssetData,
    private val createAccountPendingDeletionAssetData: CreateAccountPendingDeletionAssetData
) : CreateAccountAssetData {

    override suspend fun invoke(accountInformation: AccountInformation, includeAlgo: Boolean): AccountAssetData {
        val ownedAssetDataList = mutableListOf<OwnedAssetData>()
        val pendingAdditionAssetDataList = mutableListOf<PendingAssetData.AdditionAssetData>()
        val pendingDeletionAssetDataList = mutableListOf<PendingAssetData.DeletionAssetData>()

        if (includeAlgo) {
            ownedAssetDataList.add(createAlgoOwnedAssetData(accountInformation.amount))
        }

        accountInformation.assetHoldings.forEach { assetHolding ->
            val assetDetail = getAssetDetail(assetHolding.assetId) ?: return@forEach
            when (assetHolding.status) {
                PENDING_FOR_REMOVAL -> {
                    val assetData = createAccountPendingDeletionAssetData(assetDetail)
                    pendingDeletionAssetDataList.add(assetData)
                }
                PENDING_FOR_ADDITION -> {
                    val assetData = createAccountPendingAdditionAssetData(assetDetail)
                    pendingAdditionAssetDataList.add(assetData)
                }
                OWNED_BY_ACCOUNT -> {
                    val assetData = createAccountOwnedAssetData(assetDetail, assetHolding)
                    ownedAssetDataList.add(assetData)
                }
                PENDING_FOR_SENDING -> Unit // TODO
            }
        }

        return AccountAssetData(
            ownedAssetDataList,
            pendingAdditionAssetDataList,
            pendingDeletionAssetDataList
        )
    }
}
