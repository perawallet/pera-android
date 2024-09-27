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

package com.algorand.android.module.swap.ui.assetselection.toasset.usecase

import com.algorand.android.module.asset.utils.AssetAdditionPayload
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionItem
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionPreview
import javax.inject.Inject

internal class GetUpdatedPreviewWithAssetSelectionUseCase @Inject constructor(
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData
) : GetUpdatedPreviewWithAssetSelection {

    override suspend fun invoke(
        accountAddress: String,
        swapAssetSelectionItem: SwapAssetSelectionItem,
        previousState: SwapAssetSelectionPreview
    ): SwapAssetSelectionPreview {
        val accountAssetData = getAccountOwnedAssetData(accountAddress, swapAssetSelectionItem.assetId, true)
        val isAccountOptedInToSelectedAsset = accountAssetData != null
        return with(previousState) {
            if (isAccountOptedInToSelectedAsset) {
                copy(assetSelectedEvent = Event(swapAssetSelectionItem))
            } else {
                val assetAdditionPayload = AssetAdditionPayload(swapAssetSelectionItem.assetId, accountAddress)
                copy(navigateToAssetAdditionBottomSheetEvent = Event(assetAdditionPayload))
            }
        }
    }
}
