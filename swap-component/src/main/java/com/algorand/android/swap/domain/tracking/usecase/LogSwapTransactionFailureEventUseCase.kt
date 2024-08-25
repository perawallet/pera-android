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

package com.algorand.android.swap.domain.tracking.usecase

import com.algorand.android.eventtracker.NodeAwarePeraEventTracker
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.tracking.SwapTrackingHelper
import com.algorand.android.swap.domain.tracking.mapper.SwapFailedEventPayloadMapper
import javax.inject.Inject

internal class LogSwapTransactionFailureEventUseCase @Inject constructor(
    private val swapTrackingHelper: SwapTrackingHelper,
    private val swapFailedEventPayloadMapper: SwapFailedEventPayloadMapper,
    private val nodeAwarePeraEventTracker: NodeAwarePeraEventTracker
) : LogSwapTransactionFailureEvent {

    override fun invoke(swapQuote: SwapQuote) {
        val payload = getEventPayload(swapQuote)
        nodeAwarePeraEventTracker.logEvent(SWAP_FAILURE_EVENT_KEY, payload)
    }

    private fun getEventPayload(swapQuote: SwapQuote): Map<String, Any> {
        return with(swapQuote) {
            swapFailedEventPayloadMapper(
                swapQuote = this,
                inputAsaAmountAsAlgo = swapTrackingHelper.getInputAsaAmountAsAlgo(swapQuote),
                inputAsaAmountAsUsd = swapTrackingHelper.getInputAsaAmountAsUsd(this),
                outputAsaAmountAsAlgo = swapTrackingHelper.getOutputAsaAmountAsAlgo(swapQuote),
                outputAsaAmountAsUsd = swapTrackingHelper.getOutputAsaAmountAsUsd(this),
                swapDateTimestamp = swapTrackingHelper.getSwapDateTimestamp(),
                formattedSwapDateTime = swapTrackingHelper.getFormattedSwapDateTime()
            )
        }
    }

    private companion object {
        const val SWAP_FAILURE_EVENT_KEY = "swapscr_assets_failed"
    }
}
