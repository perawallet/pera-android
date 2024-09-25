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

package com.algorand.android.module.swap.ui.assetswap.usecase.main

import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.model.SwapError
import kotlinx.coroutines.flow.Flow

interface AssetSwapPreviewProcessor {
    suspend fun getAssetSwapPreviewInitializationState(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long?
    ): AssetSwapPreview?

    suspend fun getAmountUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long?,
        amount: String?,
        accountAddress: String,
        percentage: Float?,
        previousState: AssetSwapPreview?
    ): Flow<AssetSwapPreview>

    suspend fun getAssetsSwitchedUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): Flow<AssetSwapPreview>

    suspend fun getFromAssetUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long?,
        amount: String?,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): Flow<AssetSwapPreview>

    suspend fun getToAssetUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long,
        amount: String?,
        fromAssetDecimal: Int,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): Flow<AssetSwapPreview>

    suspend fun getBalanceForSelectedPercentage(
        previousAmount: String,
        fromAssetId: Long,
        toAssetId: Long,
        percentage: Float,
        onLoadingChange: (isLoading: Boolean) -> Unit,
        onSuccess: (amount: String) -> Unit,
        onFailure: (errorEvent: Event<SwapError>) -> Unit,
        accountAddress: String
    )

    fun getPercentageCalculationSuccessPreview(percentage: Float, previousState: AssetSwapPreview): AssetSwapPreview

    fun getPercentageCalculationFailedPreview(
        errorEvent: Event<SwapError>,
        previousState: AssetSwapPreview
    ): AssetSwapPreview

    fun getSwapButtonClickUpdatedPreview(previousState: AssetSwapPreview): AssetSwapPreview
}
