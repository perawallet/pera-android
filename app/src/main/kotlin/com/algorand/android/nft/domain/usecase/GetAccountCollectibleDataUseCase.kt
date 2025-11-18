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

package com.algorand.android.nft.domain.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import javax.inject.Inject

internal class GetAccountCollectibleDataUseCase @Inject constructor(
    private val getAccountAssetHolding: GetAccountAssetHolding,
    private val getCollectibleDetail: GetCollectibleDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectibleData {

    override suspend fun invoke(address: String, assetId: Long): BaseOwnedCollectibleData? {
        val assetHolding = getAccountAssetHolding(address, assetId) ?: return null
        val collectibleDetail = getCollectibleDetail(assetId) ?: return null
        return baseOwnedCollectibleDataFactory(assetHolding, collectibleDetail)
    }
}
