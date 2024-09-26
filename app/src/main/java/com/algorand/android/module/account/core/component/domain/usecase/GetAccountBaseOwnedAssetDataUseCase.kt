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

package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData
import javax.inject.Inject

internal class GetAccountBaseOwnedAssetDataUseCase @Inject constructor(
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getAccountCollectiblesData: GetAccountCollectiblesData
) : GetAccountBaseOwnedAssetData {

    override suspend fun invoke(address: String, assetId: Long): BaseOwnedAssetData? {
        return getAccountAssetData(address, assetId) ?: getAccountCollectibleData(address, assetId)
    }

    private suspend fun getAccountAssetData(address: String, assetId: Long): BaseOwnedAssetData? {
        return getAccountOwnedAssetData(address, assetId, true)
    }

    private suspend fun getAccountCollectibleData(address: String, assetId: Long): BaseOwnedAssetData? {
        return getAccountCollectiblesData(address).find {
            it.id == assetId
        }
    }
}
