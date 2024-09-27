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

import com.algorand.android.module.account.core.component.assetdata.model.AccountAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.PendingAssetData
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

interface GetAccountAssetDataFlow {
    operator fun invoke(address: String, includeAlgo: Boolean): Flow<AccountAssetData>
}

interface GetAccountAssetsData {
    suspend operator fun invoke(address: String, includeAlgo: Boolean): AccountAssetData
}

interface GetAccountOwnedAssetsData {
    suspend operator fun invoke(address: String, includeAlgo: Boolean): List<OwnedAssetData>
    suspend operator fun invoke(accountInformation: AccountInformation, includeAlgo: Boolean): List<OwnedAssetData>
}

interface GetAccountOwnedAssetsDataFlow {
    operator fun invoke(address: String, includeAlgo: Boolean): Flow<List<OwnedAssetData>>
}

interface GetAccountOwnedAssetData {
    suspend operator fun invoke(address: String, assetId: Long, includeAlgo: Boolean): OwnedAssetData?
}

interface GetAccountBaseOwnedAssetData {
    suspend operator fun invoke(address: String, assetId: Long): BaseOwnedAssetData?
}

internal interface CreateAlgoOwnedAssetData {
    suspend operator fun invoke(amount: BigInteger): OwnedAssetData
}

internal interface CreateAccountAssetData {
    suspend operator fun invoke(accountInformation: AccountInformation, includeAlgo: Boolean): AccountAssetData
}

internal interface CreateAccountOwnedAssetData {
    suspend operator fun invoke(assetDetail: AssetDetail, assetHolding: AssetHolding): OwnedAssetData
}

internal interface CreateAccountPendingAdditionAssetData {
    suspend operator fun invoke(assetDetail: AssetDetail): PendingAssetData.AdditionAssetData
}

internal interface CreateAccountPendingDeletionAssetData {
    suspend operator fun invoke(assetDetail: AssetDetail): PendingAssetData.DeletionAssetData
}
