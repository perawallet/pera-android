/*
 * Copyright 2022 Pera Wallet, LDA
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
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.modules.accountcore.domain.model.AccountAssetData
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.asset.domain.model.AssetDetail
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

fun interface GetAccountCollectibleDataFlow {
    operator fun invoke(address: String): Flow<List<BaseOwnedCollectibleData>>
}

fun interface GetAccountAssetDataFlow {
    operator fun invoke(address: String, includeAlgo: Boolean): Flow<AccountAssetData>
}

internal interface CreateAccountAssetData {
    suspend operator fun invoke(accountInformation: AccountInformation, includeAlgo: Boolean): AccountAssetData
}

internal interface CreateAlgoOwnedAssetData {
    suspend operator fun invoke(amount: BigInteger): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
}

internal interface CreateAccountPendingAdditionAssetData {
    suspend operator fun invoke(assetDetail: AssetDetail): BaseAccountAssetData.PendingAssetData.AdditionAssetData
}

internal interface CreateAccountPendingDeletionAssetData {
    suspend operator fun invoke(assetDetail: AssetDetail): BaseAccountAssetData.PendingAssetData.DeletionAssetData
}
