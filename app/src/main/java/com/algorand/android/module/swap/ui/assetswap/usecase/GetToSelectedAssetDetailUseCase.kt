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

package com.algorand.android.module.swap.ui.assetswap.usecase

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAsset
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import javax.inject.Inject

internal class GetToSelectedAssetDetailUseCase @Inject constructor(
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getSelectedAssetDetail: GetSelectedAssetDetail,
    private val fetchAsset: FetchAsset
) : GetToSelectedAssetDetail {

    override suspend fun invoke(
        toAssetId: Long?,
        accountAddress: String,
        selectedAssetDetail: SelectedAssetDetail?
    ): SelectedAssetDetail? {
        val isToAssetHasChanged = selectedAssetDetail?.assetId != toAssetId
        if (toAssetId == null) return null
        return if (isToAssetHasChanged) {
            getAssetDetail(accountAddress, toAssetId)
        } else {
            selectedAssetDetail
        }
    }

    override suspend fun invoke(toAssetId: Long?, accountAddress: String): SelectedAssetDetail? {
        if (toAssetId == null) return null
        return getAssetDetail(accountAddress, toAssetId)
    }

    private suspend fun getAssetDetail(accountAddress: String, toAssetId: Long): SelectedAssetDetail? {
        val ownedAssetData = getAccountOwnedAssetData(accountAddress, toAssetId, includeAlgo = true)
        return if (ownedAssetData == null) {
            val assetDetail = fetchAsset(toAssetId).getDataOrNull() as? AssetDetail ?: return null
            getSelectedAssetDetail(assetDetail)
        } else {
            getSelectedAssetDetail(ownedAssetData)
        }
    }
}
