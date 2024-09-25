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

import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.common.toBigDecimalOrZero
import com.algorand.android.module.swap.component.domain.model.GetSwapQuotePayload
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.usecase.GetSwapQuote
import com.algorand.android.module.swap.ui.GetSwapError
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.model.GetSwapQuoteUpdatedPreviewPayload
import java.math.BigInteger
import javax.inject.Inject

internal class GetSwapQuoteUpdatedPreviewUseCase @Inject constructor(
    private val getSwapQuote: GetSwapQuote,
    private val getSwapBalanceError: GetSwapBalanceError,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getFromSelectedAssetAmountDetail: GetFromSelectedAssetAmountDetail,
    private val getToSelectedAssetAmountDetail: GetToSelectedAssetAmountDetail,
    private val getSelectedAssetDetail: GetSelectedAssetDetail,
    private val getAssetSwapSwitchButtonStatus: GetAssetSwapSwitchButtonStatus,
    private val getSwapError: GetSwapError
) : GetSwapQuoteUpdatedPreview {

    override suspend fun invoke(payload: GetSwapQuoteUpdatedPreviewPayload): AssetSwapPreview {
        return getSwapQuote(getSwapQuotePayload(payload)).use(
            onSuccess = { swapQuote ->
                getSuccessState(swapQuote, payload)
            },
            onFailed = { exception, _ ->
                getErrorState(payload, exception.message)
            }
        )
    }

    private suspend fun getSuccessState(
        swapQuote: SwapQuote,
        payload: GetSwapQuoteUpdatedPreviewPayload
    ): AssetSwapPreview {
        val errorEvent = getSwapBalanceError(swapQuote, payload.accountAddress)
        return AssetSwapPreview(
            accountDisplayName = getAccountDisplayName(payload.accountAddress),
            accountIconDrawablePreview = getAccountIconDrawablePreview(payload.accountAddress),
            fromSelectedAssetDetail = getSelectedAssetDetail(payload.accountAddress, swapQuote.fromAssetDetail),
            toSelectedAssetDetail = getSelectedAssetDetail(payload.accountAddress, swapQuote.toAssetDetail),
            isSwapButtonEnabled = errorEvent == null,
            isLoadingVisible = false,
            fromSelectedAssetAmountDetail = getFromSelectedAssetAmountDetail(swapQuote, payload.amount),
            toSelectedAssetAmountDetail = getToSelectedAssetAmountDetail(swapQuote),
            isSwitchAssetsButtonEnabled = isSwitchAssetsButtonEnabled(payload),
            isMaxAndPercentageButtonEnabled = payload.isMaxAndPercentageButtonEnabled,
            formattedPercentageText = payload.formattedPercentageText,
            errorEvent = errorEvent,
            swapQuote = swapQuote,
            clearToSelectedAssetDetailEvent = null,
            navigateToConfirmSwapFragmentEvent = null
        )
    }

    private suspend fun isSwitchAssetsButtonEnabled(payload: GetSwapQuoteUpdatedPreviewPayload): Boolean {
        return getAssetSwapSwitchButtonStatus(
            accountAddress = payload.accountAddress,
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId,
            fromSelectedAssetDetail = payload.previousState.fromSelectedAssetDetail
        )
    }

    private suspend fun getErrorState(
        payload: GetSwapQuoteUpdatedPreviewPayload,
        errorMessage: String?
    ): AssetSwapPreview {
        return payload.previousState.copy(
            isLoadingVisible = false,
            isSwapButtonEnabled = false,
            errorEvent = errorMessage?.run {
                Event(
                    getSwapError
                        (this)
                )
            },
            fromSelectedAssetAmountDetail = payload.previousState.swapQuote?.run {
                getFromSelectedAssetAmountDetail(this, payload.amount)
            }
        )
    }

    private fun getSwapQuotePayload(payload: GetSwapQuoteUpdatedPreviewPayload): GetSwapQuotePayload {
        return GetSwapQuotePayload(
            fromAssetId = payload.fromAssetId,
            toAssetId = payload.toAssetId,
            amount = getAmountAsBigInteger(payload),
            slippage = null,
            accountAddress = payload.accountAddress
        )
    }

    private fun getAmountAsBigInteger(payload: GetSwapQuoteUpdatedPreviewPayload): BigInteger {
        return payload.amount.toBigDecimalOrZero().movePointRight(payload.swapTypeAssetDecimal).toBigInteger()
    }
}
