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

import com.algorand.android.module.foundation.common.isEqualTo
import com.algorand.android.module.foundation.common.toBigDecimalOrZero
import com.algorand.android.module.parity.domain.usecase.GetDisplayedCurrencySymbol
import com.algorand.android.module.swap.component.domain.SwapAmountUtils
import com.algorand.android.module.swap.ui.assetswap.mapper.GetSwapQuoteUpdatedPreviewPayloadMapper
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.usecase.main.GetSwapAmountUpdatedPreview
import com.algorand.android.module.swap.ui.assetswap.usecase.main.model.SwapAmountUpdatedPreviewPayload
import com.algorand.android.utils.formatAsCurrency
import java.math.BigDecimal.ZERO
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetSwapAmountUpdatedPreviewUseCase @Inject constructor(
    private val getSelectedAssetAmountDetail: GetSelectedAssetAmountDetail,
    private val getDisplayedCurrencySymbol: GetDisplayedCurrencySymbol,
    private val getSwapQuoteUpdatedPreview: GetSwapQuoteUpdatedPreview,
    private val getSwapQuoteUpdatedPreviewPayloadMapper: GetSwapQuoteUpdatedPreviewPayloadMapper
) : GetSwapAmountUpdatedPreview {

    override suspend fun invoke(payload: SwapAmountUpdatedPreviewPayload): Flow<AssetSwapPreview> = flow {
        with(payload) {
            if (previousState != null) {
                if (amount == null || amount.toBigDecimalOrZero() isEqualTo ZERO) {
                    val newState = previousState.copy(
                        toSelectedAssetAmountDetail = getSelectedAssetAmountDetail(
                            primaryCurrencySymbol = getDisplayedCurrencySymbol()
                        ),
                        fromSelectedAssetAmountDetail = previousState.fromSelectedAssetAmountDetail?.copy(
                            amount = amount,
                            formattedApproximateValue = ZERO.formatAsCurrency(getDisplayedCurrencySymbol())
                        ),
                        isSwapButtonEnabled = false,
                        isLoadingVisible = false,
                        errorEvent = null
                    )
                    emit(newState)
                    return@flow
                }
                if (toAssetId == null || !SwapAmountUtils.isAmountValidForApiRequest(amount)) {
                    emit(previousState.copy(isLoadingVisible = false))
                    return@flow
                }
                emit(previousState.copy(isLoadingVisible = true))
                val swapQuoteUpdatedPayload = getSwapQuoteUpdatedPreviewPayloadMapper(payload)
                val preview = getSwapQuoteUpdatedPreview(swapQuoteUpdatedPayload) ?: return@flow
                emit(preview)
            }
        }
    }
}
