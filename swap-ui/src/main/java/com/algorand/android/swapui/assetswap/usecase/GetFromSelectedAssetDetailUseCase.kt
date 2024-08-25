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

package com.algorand.android.swapui.assetswap.usecase

import com.algorand.android.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.swapui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import javax.inject.Inject

internal class GetFromSelectedAssetDetailUseCase @Inject constructor(
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getSelectedAssetDetail: GetSelectedAssetDetail
) : GetFromSelectedAssetDetail {

    override suspend fun invoke(
        fromAssetId: Long,
        accountAddress: String,
        selectedAssetDetail: SelectedAssetDetail
    ): SelectedAssetDetail {
        val isFromAssetHasChanged = selectedAssetDetail.assetId != fromAssetId
        return if (isFromAssetHasChanged) {
            val ownedAssetData = getOwnedAssetData(accountAddress, fromAssetId)
            getSelectedAssetDetail(ownedAssetData)
        } else {
            selectedAssetDetail
        }
    }

    override suspend fun invoke(fromAssetId: Long, accountAddress: String): SelectedAssetDetail {
        val ownedAssetData = getOwnedAssetData(accountAddress, fromAssetId)
        return getSelectedAssetDetail(ownedAssetData)
    }

    private suspend fun getOwnedAssetData(accountAddress: String, fromAssetId: Long): OwnedAssetData {
        return getAccountOwnedAssetData(accountAddress, fromAssetId, includeAlgo = true)!!
    }
}
