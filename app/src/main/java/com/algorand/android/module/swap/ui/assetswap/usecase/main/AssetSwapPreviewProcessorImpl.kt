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

import com.algorand.android.module.foundation.Event
import com.algorand.android.module.swap.component.domain.exceptions.InsufficientAlgoBalance
import com.algorand.android.module.swap.component.domain.model.GetPercentageCalculatedBalanceForSwapPayload
import com.algorand.android.module.swap.component.domain.usecase.GetPercentageCalculatedBalanceForSwap
import com.algorand.android.module.swap.ui.GetSwapError
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.model.GetToAssetUpdatedPreviewPayload
import com.algorand.android.module.swap.ui.assetswap.model.SwapError
import com.algorand.android.module.swap.ui.assetswap.usecase.main.model.AssetsSwitchedUpdatedPreviewPayload
import com.algorand.android.module.swap.ui.assetswap.usecase.main.model.FromAssetUpdatedPreviewPayload
import com.algorand.android.module.swap.ui.assetswap.usecase.main.model.SwapAmountUpdatedPreviewPayload
import com.algorand.android.utils.formatAsPercentage
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class AssetSwapPreviewProcessorImpl @Inject constructor(
    private val getSwapInitialPreview: GetSwapInitialPreview,
    private val getSwapAmountUpdatedPreview: GetSwapAmountUpdatedPreview,
    private val getAssetsSwitchedUpdatedPreview: GetAssetsSwitchedUpdatedPreview,
    private val getFromAssetUpdatedPreview: GetFromAssetUpdatedPreview,
    private val getToAssetUpdatedPreview: GetToAssetUpdatedPreview,
    private val getPercentageCalculatedBalanceForSwap: GetPercentageCalculatedBalanceForSwap,
    private val getSwapError: GetSwapError
) : AssetSwapPreviewProcessor {

    override suspend fun getAssetSwapPreviewInitializationState(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long?
    ): AssetSwapPreview? {
        return getSwapInitialPreview(accountAddress, fromAssetId, toAssetId)
    }

    override suspend fun getAmountUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long?,
        amount: String?,
        accountAddress: String,
        percentage: Float?,
        previousState: AssetSwapPreview?
    ): Flow<AssetSwapPreview> {
        val payload = SwapAmountUpdatedPreviewPayload(
            fromAssetId = fromAssetId,
            toAssetId = toAssetId,
            amount = amount,
            accountAddress = accountAddress,
            percentage = percentage,
            previousState = previousState
        )
        return getSwapAmountUpdatedPreview(payload)
    }

    override suspend fun getAssetsSwitchedUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): Flow<AssetSwapPreview> {
        val payload = AssetsSwitchedUpdatedPreviewPayload(
            fromAssetId = fromAssetId,
            toAssetId = toAssetId,
            accountAddress = accountAddress,
            previousState = previousState
        )
        return getAssetsSwitchedUpdatedPreview(payload)
    }

    override suspend fun getFromAssetUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long?,
        amount: String?,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): Flow<AssetSwapPreview> {
        val payload = FromAssetUpdatedPreviewPayload(
            fromAssetId = fromAssetId,
            toAssetId = toAssetId,
            amount = amount,
            accountAddress = accountAddress,
            previousState = previousState
        )
        return getFromAssetUpdatedPreview(payload)
    }

    override suspend fun getToAssetUpdatedPreview(
        fromAssetId: Long,
        toAssetId: Long,
        amount: String?,
        fromAssetDecimal: Int,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): Flow<AssetSwapPreview> {
        val payload = GetToAssetUpdatedPreviewPayload(
            fromAssetId = fromAssetId,
            toAssetId = toAssetId,
            amount = amount,
            fromAssetDecimal = fromAssetDecimal,
            accountAddress = accountAddress,
            previousState = previousState
        )
        return getToAssetUpdatedPreview(payload)
    }

    override suspend fun getBalanceForSelectedPercentage(
        previousAmount: String,
        fromAssetId: Long,
        toAssetId: Long,
        percentage: Float,
        onLoadingChange: (isLoading: Boolean) -> Unit,
        onSuccess: (amount: String) -> Unit,
        onFailure: (errorEvent: Event<SwapError>) -> Unit,
        accountAddress: String
    ) {
        onLoadingChange(true)
        val payload = GetPercentageCalculatedBalanceForSwapPayload(fromAssetId, toAssetId, percentage, accountAddress)
        getPercentageCalculatedBalanceForSwap(payload).use(
            onSuccess = { updatedAmount ->
                if (previousAmount == updatedAmount.toString()) onLoadingChange(false)
                onSuccess(updatedAmount.stripTrailingZeros().toPlainString())
            },
            onFailed = { exception, code ->
                val errorEvent = if (exception is InsufficientAlgoBalance) {
                    // Event(ErrorResource.LocalErrorResource.Local(R.string.account_does_not_have_algo))
                    Event(getSwapError("TODO"))
                } else {
                    // Event(ErrorResource.Api(it.exception?.message.orEmpty()))
                    Event(getSwapError("TODO"))
                }
                onFailure(errorEvent)
            }
        )
    }

    override fun getSwapButtonClickUpdatedPreview(previousState: AssetSwapPreview): AssetSwapPreview {
        if (previousState.swapQuote == null) {
            return previousState
        }
        return previousState.copy(
            navigateToConfirmSwapFragmentEvent = Event(previousState.swapQuote)
        )
    }

    override fun getPercentageCalculationSuccessPreview(
        percentage: Float,
        previousState: AssetSwapPreview
    ): AssetSwapPreview {
        return previousState.copy(
            formattedPercentageText = percentage.formatAsPercentage()
        )
    }

    override fun getPercentageCalculationFailedPreview(
        errorEvent: Event<SwapError>,
        previousState: AssetSwapPreview
    ): AssetSwapPreview {
        return previousState.copy(
            errorEvent = errorEvent,
            isLoadingVisible = false
        )
    }
}
