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

import com.algorand.android.parity.domain.usecase.GetDisplayedCurrencySymbol
import com.algorand.android.module.swap.component.domain.SwapAmountUtils
import com.algorand.android.module.swap.ui.assetswap.mapper.GetSwapQuoteUpdatedPreviewPayloadMapper
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetAmountDetail
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import com.algorand.android.module.swap.ui.assetswap.model.GetToAssetUpdatedPreviewPayload
import com.algorand.android.module.swap.ui.assetswap.usecase.main.GetToAssetUpdatedPreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetToAssetUpdatedPreviewUseCase @Inject constructor(
    private val getToSelectedAssetDetail: GetToSelectedAssetDetail,
    private val getSelectedAssetAmountDetail: GetSelectedAssetAmountDetail,
    private val getDisplayedCurrencySymbol: GetDisplayedCurrencySymbol,
    private val getAssetSwapSwitchButtonStatus: GetAssetSwapSwitchButtonStatus,
    private val getSwapQuoteUpdatedPreview: GetSwapQuoteUpdatedPreview,
    private val getSwapQuoteUpdatedPreviewPayloadMapper: GetSwapQuoteUpdatedPreviewPayloadMapper
) : GetToAssetUpdatedPreview {

    override suspend fun invoke(payload: GetToAssetUpdatedPreviewPayload): Flow<AssetSwapPreview> = flow {
        with(payload) {
            emit(previousState.copy(isLoadingVisible = true))
            val amountAsBigDecimal = amount?.toBigDecimalOrNull()
            if (amountAsBigDecimal == null) {
                val newState = getPreviewForNullAmount(payload)
                emit(newState)
            } else {
                if (!SwapAmountUtils.isAmountValidForApiRequest(amount)) return@flow
                val swapQuoteUpdatedPreviewPayload = getSwapQuoteUpdatedPreviewPayloadMapper(payload)
                val swapQuoteUpdatedPreview = getSwapQuoteUpdatedPreview(swapQuoteUpdatedPreviewPayload) ?: return@flow
                emit(swapQuoteUpdatedPreview)
            }
        }
    }

    private suspend fun getPreviewForNullAmount(payload: GetToAssetUpdatedPreviewPayload): AssetSwapPreview {
        val toSelectedAssetAmountDetail = getToSelectedAssetAmountDetail()
        val toSelectedAssetDetail = getToSelectedAssetDetail(payload)
        return payload.previousState.copy(
            toSelectedAssetDetail = toSelectedAssetDetail,
            isLoadingVisible = false,
            isSwitchAssetsButtonEnabled = isSwitchAssetsButtonEnabled(payload),
            isMaxAndPercentageButtonEnabled = true,
            toSelectedAssetAmountDetail = toSelectedAssetAmountDetail
        )
    }

    private suspend fun getToSelectedAssetDetail(payload: GetToAssetUpdatedPreviewPayload): SelectedAssetDetail? {
        return getToSelectedAssetDetail(
            toAssetId = payload.toAssetId,
            accountAddress = payload.accountAddress,
            selectedAssetDetail = payload.previousState.toSelectedAssetDetail
        )
    }

    private suspend fun getToSelectedAssetAmountDetail(): SelectedAssetAmountDetail {
        return getSelectedAssetAmountDetail(primaryCurrencySymbol = getDisplayedCurrencySymbol())
    }

    private suspend fun isSwitchAssetsButtonEnabled(payload: GetToAssetUpdatedPreviewPayload): Boolean {
        return getAssetSwapSwitchButtonStatus(
            accountAddress = payload.accountAddress,
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId,
            fromSelectedAssetDetail = payload.previousState.fromSelectedAssetDetail
        )
    }
}
