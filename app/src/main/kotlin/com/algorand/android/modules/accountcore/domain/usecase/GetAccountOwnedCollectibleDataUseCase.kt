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

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import javax.inject.Inject

internal class GetAccountOwnedCollectibleDataUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getCollectibleDetail: GetCollectibleDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountOwnedCollectibleData {

    override suspend fun invoke(address: String, collectibleId: Long): BaseOwnedAssetData.BaseOwnedCollectibleData? {
        val accountInfo = getAccountInformation(address) ?: return null
        val assetHolding = accountInfo.assetHoldings.find { it.assetId == collectibleId } ?: return null
        val collectibleDetail = getCollectibleDetail(collectibleId) ?: return null
        return baseOwnedCollectibleDataFactory(assetHolding, collectibleDetail)
    }
}
