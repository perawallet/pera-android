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

import com.algorand.android.parity.domain.usecase.GetDisplayedCurrencySymbol
import com.algorand.android.module.swap.component.domain.SwapAmountUtils
import com.algorand.android.module.swap.ui.assetswap.mapper.GetSwapQuoteUpdatedPreviewPayloadMapper
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.usecase.GetFromSelectedAssetDetail
import com.algorand.android.module.swap.ui.assetswap.usecase.GetSelectedAssetAmountDetail
import com.algorand.android.module.swap.ui.assetswap.usecase.GetSwapQuoteUpdatedPreview
import com.algorand.android.module.swap.ui.assetswap.usecase.main.model.FromAssetUpdatedPreviewPayload
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetFromAssetUpdatedPreviewUseCase @Inject constructor(
    private val getFromSelectedAssetDetail: GetFromSelectedAssetDetail,
    private val getSwapQuoteUpdatedPreview: GetSwapQuoteUpdatedPreview,
    private val getSelectedAssetAmountDetail: GetSelectedAssetAmountDetail,
    private val getDisplayedCurrencySymbol: GetDisplayedCurrencySymbol,
    private var getSwapQuoteUpdatedPreviewPayloadMapper: GetSwapQuoteUpdatedPreviewPayloadMapper
) : GetFromAssetUpdatedPreview {

    override suspend fun invoke(payload: FromAssetUpdatedPreviewPayload): Flow<AssetSwapPreview> = flow {
        with(payload) {
            val fromSelectedAssetDetail = getFromSelectedAssetDetail(
                fromAssetId = fromAssetId,
                accountAddress = accountAddress,
                selectedAssetDetail = previousState.fromSelectedAssetDetail
            )
            val newState = payload.previousState.copy(fromSelectedAssetDetail = fromSelectedAssetDetail)

            if (toAssetId == fromAssetId) {
                emit(getToAssetClearedState(newState))
                return@flow
            }

            if (payload.toAssetId == null || amount?.toBigDecimalOrNull() == null) {
                emit(newState)
            } else {
                if (!SwapAmountUtils.isAmountValidForApiRequest(amount)) return@flow
                emit(previousState.copy(isLoadingVisible = true))
                val swapQuoteUpdatedPayload = getSwapQuoteUpdatedPreviewPayloadMapper(payload)
                val swapQuoteUpdatedPreview = getSwapQuoteUpdatedPreview(swapQuoteUpdatedPayload) ?: return@flow
                emit(swapQuoteUpdatedPreview)
            }
        }
    }

    private suspend fun getToAssetClearedState(previousState: AssetSwapPreview): AssetSwapPreview {
        return previousState.copy(
            toSelectedAssetDetail = null,
            clearToSelectedAssetDetailEvent = null,
            toSelectedAssetAmountDetail = getSelectedAssetAmountDetail(
                amount = null,
                primaryCurrencySymbol = getDisplayedCurrencySymbol()
            ),
            fromSelectedAssetAmountDetail = getSelectedAssetAmountDetail(
                amount = previousState.fromSelectedAssetAmountDetail?.amount,
                assetDecimal = previousState.fromSelectedAssetAmountDetail?.assetDecimal,
                primaryCurrencySymbol = getDisplayedCurrencySymbol()
            ),
            isSwapButtonEnabled = false,
            isMaxAndPercentageButtonEnabled = false,
            isSwitchAssetsButtonEnabled = false
        )
    }
}
