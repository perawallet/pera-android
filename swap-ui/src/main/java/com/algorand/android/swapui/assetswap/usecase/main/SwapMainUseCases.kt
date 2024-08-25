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

package com.algorand.android.swapui.assetswap.usecase.main

import com.algorand.android.swapui.assetswap.model.AssetSwapPreview
import com.algorand.android.swapui.assetswap.model.GetToAssetUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.AssetsSwitchedUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.FromAssetUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.SwapAmountUpdatedPreviewPayload
import kotlinx.coroutines.flow.Flow

internal interface GetSwapInitialPreview {
    suspend operator fun invoke(accountAddress: String, fromAssetId: Long, toAssetId: Long?): AssetSwapPreview?
}

internal interface GetSwapAmountUpdatedPreview {
    suspend operator fun invoke(payload: SwapAmountUpdatedPreviewPayload): Flow<AssetSwapPreview>
}

internal interface GetAssetsSwitchedUpdatedPreview {
    suspend operator fun invoke(payload: AssetsSwitchedUpdatedPreviewPayload): Flow<AssetSwapPreview>
}

internal interface GetFromAssetUpdatedPreview {
    suspend operator fun invoke(payload: FromAssetUpdatedPreviewPayload): Flow<AssetSwapPreview>
}

internal interface GetToAssetUpdatedPreview {
    suspend operator fun invoke(payload: GetToAssetUpdatedPreviewPayload): Flow<AssetSwapPreview>
}
