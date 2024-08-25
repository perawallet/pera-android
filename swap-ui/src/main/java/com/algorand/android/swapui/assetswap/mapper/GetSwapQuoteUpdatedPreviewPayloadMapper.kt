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

package com.algorand.android.swapui.assetswap.mapper

import com.algorand.android.swapui.assetswap.model.AssetSwapPreview
import com.algorand.android.swapui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import com.algorand.android.swapui.assetswap.model.GetSwapQuoteUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.model.GetToAssetUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.AssetsSwitchedUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.FromAssetUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.SwapAmountUpdatedPreviewPayload

internal interface GetSwapQuoteUpdatedPreviewPayloadMapper {
    suspend operator fun invoke(payload: GetToAssetUpdatedPreviewPayload): GetSwapQuoteUpdatedPreviewPayload
    suspend operator fun invoke(payload: SwapAmountUpdatedPreviewPayload): GetSwapQuoteUpdatedPreviewPayload
    suspend operator fun invoke(payload: FromAssetUpdatedPreviewPayload): GetSwapQuoteUpdatedPreviewPayload
    suspend operator fun invoke(
        payload: AssetsSwitchedUpdatedPreviewPayload,
        newState: AssetSwapPreview,
        fromAssetDetail: SelectedAssetDetail
    ): GetSwapQuoteUpdatedPreviewPayload
}
