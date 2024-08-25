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

import com.algorand.android.formatting.formatAsPercentage
import com.algorand.android.swapui.assetswap.model.AssetSwapPreview
import com.algorand.android.swapui.assetswap.model.GetSwapQuoteUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.model.GetToAssetUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.AssetsSwitchedUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.FromAssetUpdatedPreviewPayload
import com.algorand.android.swapui.assetswap.usecase.main.model.SwapAmountUpdatedPreviewPayload
import javax.inject.Inject

internal class GetSwapQuoteUpdatedPreviewPayloadMapperImpl @Inject constructor() :
    GetSwapQuoteUpdatedPreviewPayloadMapper {

    override suspend fun invoke(payload: GetToAssetUpdatedPreviewPayload): GetSwapQuoteUpdatedPreviewPayload {
        return GetSwapQuoteUpdatedPreviewPayload(
            accountAddress = payload.accountAddress,
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId,
            amount = payload.amount,
            swapTypeAssetDecimal = payload.fromAssetDecimal,
            isMaxAndPercentageButtonEnabled = true,
            formattedPercentageText = "",
            previousState = payload.previousState
        )
    }

    override suspend fun invoke(payload: SwapAmountUpdatedPreviewPayload): GetSwapQuoteUpdatedPreviewPayload {
        return with(payload) {
            GetSwapQuoteUpdatedPreviewPayload(
                accountAddress = accountAddress,
                fromAssetId = fromAssetId,
                toAssetId = toAssetId!!,
                amount = amount,
                previousState = previousState!!,
                swapTypeAssetDecimal = previousState.fromSelectedAssetDetail.assetDecimal,
                isMaxAndPercentageButtonEnabled = true,
                formattedPercentageText = percentage?.formatAsPercentage().orEmpty()
            )
        }
    }

    override suspend fun invoke(
        payload: AssetsSwitchedUpdatedPreviewPayload,
        newState: AssetSwapPreview,
        fromAssetDetail: AssetSwapPreview.SelectedAssetDetail
    ): GetSwapQuoteUpdatedPreviewPayload {
        return GetSwapQuoteUpdatedPreviewPayload(
            accountAddress = payload.accountAddress,
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId,
            amount = payload.previousState.toSelectedAssetAmountDetail?.amount,
            previousState = newState,
            swapTypeAssetDecimal = fromAssetDetail.assetDecimal,
            isMaxAndPercentageButtonEnabled = true,
            formattedPercentageText = ""
        )
    }

    override suspend fun invoke(payload: FromAssetUpdatedPreviewPayload): GetSwapQuoteUpdatedPreviewPayload {
        return GetSwapQuoteUpdatedPreviewPayload(
            accountAddress = payload.accountAddress,
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId!!,
            amount = payload.amount,
            previousState = payload.previousState,
            swapTypeAssetDecimal = payload.previousState.fromSelectedAssetDetail.assetDecimal,
            isMaxAndPercentageButtonEnabled = true,
            formattedPercentageText = ""
        )
    }
}
