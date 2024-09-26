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

package com.algorand.android.module.asset.action.ui.usecase

import com.algorand.android.module.asset.action.ui.mapper.AssetActionInformationMapper
import com.algorand.android.module.asset.action.ui.model.AssetActionInformation
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAsset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.foundation.PeraResult
import javax.inject.Inject

internal class GetAssetActionInformationUseCase @Inject constructor(
    private val getAsset: GetAsset,
    private val fetchAsset: FetchAsset,
    private val assetActionInformationMapper: AssetActionInformationMapper
) : GetAssetActionInformation {

    override suspend fun invoke(assetId: Long): PeraResult<AssetActionInformation> {
        return getAssetInfoFromCache(assetId) ?: fetchAssetInformation(assetId)
    }

    private suspend fun getAssetInfoFromCache(assetId: Long): PeraResult<AssetActionInformation>? {
        return getAsset(assetId)?.let { assetDetail ->
            PeraResult.Success(assetActionInformationMapper(assetDetail))
        }
    }

    private suspend fun fetchAssetInformation(assetId: Long): PeraResult<AssetActionInformation> {
        return fetchAsset(assetId).map { assetActionInformationMapper(it) }
    }
}
