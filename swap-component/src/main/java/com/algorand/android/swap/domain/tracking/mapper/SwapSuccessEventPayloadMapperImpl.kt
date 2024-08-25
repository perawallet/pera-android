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

package com.algorand.android.swap.domain.tracking.mapper

import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.EXCHANGE_FEE_IN_ALGO_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.INPUT_ASA_AMOUNT_AS_ALGO_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.INPUT_ASA_AMOUNT_AS_ASA_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.INPUT_ASA_AMOUNT_AS_USD_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.INPUT_ASA_ID_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.INPUT_ASA_NAME_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.NETWORK_FEE_IN_ALGO_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.OUTPUT_ASA_AMOUNT_AS_ALGO_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.OUTPUT_ASA_AMOUNT_AS_ASA_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.OUTPUT_ASA_AMOUNT_AS_USD_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.OUTPUT_ASA_ID_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.OUTPUT_ASA_NAME_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.PERA_FEE_IN_ALGO_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.PERA_FEE_IN_USD_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.SWAP_DATE_FORMATTED_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.SWAP_DATE_TIMESTAMP_PAYLOAD_KEY
import com.algorand.android.swap.domain.tracking.SwapConfirmedEventTrackerConstants.SWAP_WALLET_ADDRESS_PAYLOAD_KEY
import javax.inject.Inject

internal class SwapSuccessEventPayloadMapperImpl @Inject constructor() : SwapSuccessEventPayloadMapper {

    override fun invoke(
        swapQuote: SwapQuote,
        inputAsaAmountAsAlgo: Double,
        inputAsaAmountAsUsd: Double,
        inputAsaAmount: Double,
        outputAsaAmountAsAlgo: Double,
        outputAsaAmountAsUsd: Double,
        outputAsaAmount: Double,
        swapDateTimestamp: Long,
        formattedSwapDateTime: String,
        peraFeeAsUsd: Double,
        peraFeeAsAlgo: Double,
        exchangeFeeAsAlgo: Double,
        networkFeeAsAlgo: Double
    ): Map<String, Any> {
        return mapOf(
            INPUT_ASA_AMOUNT_AS_USD_PAYLOAD_KEY to inputAsaAmountAsUsd,
            INPUT_ASA_AMOUNT_AS_ALGO_PAYLOAD_KEY to inputAsaAmountAsAlgo,
            INPUT_ASA_AMOUNT_AS_ASA_PAYLOAD_KEY to inputAsaAmount,
            INPUT_ASA_ID_PAYLOAD_KEY to swapQuote.fromAssetDetail.assetId,
            INPUT_ASA_NAME_PAYLOAD_KEY to swapQuote.fromAssetDetail.name,
            OUTPUT_ASA_AMOUNT_AS_USD_PAYLOAD_KEY to outputAsaAmountAsUsd,
            OUTPUT_ASA_AMOUNT_AS_ALGO_PAYLOAD_KEY to outputAsaAmountAsAlgo,
            OUTPUT_ASA_AMOUNT_AS_ASA_PAYLOAD_KEY to outputAsaAmount,
            OUTPUT_ASA_ID_PAYLOAD_KEY to swapQuote.toAssetDetail.assetId,
            OUTPUT_ASA_NAME_PAYLOAD_KEY to swapQuote.toAssetDetail.name,
            SWAP_DATE_TIMESTAMP_PAYLOAD_KEY to swapDateTimestamp,
            SWAP_DATE_FORMATTED_PAYLOAD_KEY to formattedSwapDateTime,
            PERA_FEE_IN_ALGO_PAYLOAD_KEY to peraFeeAsAlgo,
            PERA_FEE_IN_USD_PAYLOAD_KEY to peraFeeAsUsd,
            EXCHANGE_FEE_IN_ALGO_PAYLOAD_KEY to exchangeFeeAsAlgo,
            NETWORK_FEE_IN_ALGO_PAYLOAD_KEY to networkFeeAsAlgo,
            SWAP_WALLET_ADDRESS_PAYLOAD_KEY to swapQuote.accountAddress
        )
    }
}
