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

import com.algorand.android.swap.domain.SwapAmountUtils
import com.algorand.android.module.swap.ui.assetswap.mapper.GetSwapQuoteUpdatedPreviewPayloadMapper
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import com.algorand.android.module.swap.ui.assetswap.usecase.main.GetAssetsSwitchedUpdatedPreview
import com.algorand.android.module.swap.ui.assetswap.usecase.main.model.AssetsSwitchedUpdatedPreviewPayload
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetAssetsSwitchedUpdatedPreviewUseCase @Inject constructor(
    private val getFromSelectedAssetDetail: GetFromSelectedAssetDetail,
    private val getToSelectedAssetDetail: GetToSelectedAssetDetail,
    private val getAssetSwapSwitchButtonStatus: GetAssetSwapSwitchButtonStatus,
    private val getSwapQuoteUpdatedPreview: GetSwapQuoteUpdatedPreview,
    private val getSwapQuoteUpdatedPreviewPayloadMapper: GetSwapQuoteUpdatedPreviewPayloadMapper
) : GetAssetsSwitchedUpdatedPreview {

    override suspend fun invoke(payload: AssetsSwitchedUpdatedPreviewPayload): Flow<AssetSwapPreview> = flow {
        with(payload) {
            emit(previousState.copy(isLoadingVisible = true))
            val fromAssetDetail =
                getFromSelectedAssetDetail(fromAssetId, accountAddress, previousState.fromSelectedAssetDetail)
            val newState = getNewPreview(toAssetId, accountAddress, fromAssetDetail, previousState)

            val amount = previousState.toSelectedAssetAmountDetail?.amount
            if (!SwapAmountUtils.isAmountValidForApiRequest(amount)) {
                emit(newState)
            } else {
                val swapQuoteUpdatePayload = getSwapQuoteUpdatedPreviewPayloadMapper(payload, newState, fromAssetDetail)
                val swapQuoteUpdatedPreview = getSwapQuoteUpdatedPreview(swapQuoteUpdatePayload) ?: return@flow
                emit(swapQuoteUpdatedPreview)
            }
        }
    }

    private suspend fun getNewPreview(
        toAssetId: Long,
        accountAddress: String,
        fromAssetDetail: SelectedAssetDetail,
        previousState: AssetSwapPreview
    ): AssetSwapPreview {
        val toAssetDetail = getToSelectedAssetDetail(toAssetId, accountAddress, previousState.toSelectedAssetDetail)
        return previousState.copy(
            fromSelectedAssetDetail = fromAssetDetail,
            toSelectedAssetDetail = toAssetDetail,
            isLoadingVisible = false,
            fromSelectedAssetAmountDetail = previousState.toSelectedAssetAmountDetail,
            toSelectedAssetAmountDetail = previousState.fromSelectedAssetAmountDetail,
            isSwitchAssetsButtonEnabled = getAssetSwapSwitchButtonStatus(
                accountAddress = accountAddress,
                fromAssetId = fromAssetDetail.assetId,
                toAssetId = toAssetId,
                fromSelectedAssetDetail = previousState.fromSelectedAssetDetail
            )
        )
    }
}
