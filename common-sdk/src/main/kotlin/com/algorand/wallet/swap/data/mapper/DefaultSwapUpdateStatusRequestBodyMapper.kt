/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.swap.data.mapper

import com.algorand.wallet.swap.data.model.SwapStatusFailureReasonResponse
import com.algorand.wallet.swap.data.model.SwapStatusResponse
import com.algorand.wallet.swap.data.model.SwapUpdateStatusRequestBody
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import javax.inject.Inject

internal class DefaultSwapUpdateStatusRequestBodyMapper @Inject constructor() : SwapUpdateStatusRequestBodyMapper {

    override fun mapToInProgress(): SwapUpdateStatusRequestBody {
        return SwapUpdateStatusRequestBody(status = SwapStatusResponse.IN_PROGRESS)
    }

    override fun mapToFailed(reason: SwapStatusFailureReason): SwapUpdateStatusRequestBody {
        return SwapUpdateStatusRequestBody(
            status = SwapStatusResponse.FAILED,
            reason = getFailureReasonResponse(reason)
        )
    }

    private fun getFailureReasonResponse(reason: SwapStatusFailureReason): SwapStatusFailureReasonResponse {
        return when (reason) {
            SwapStatusFailureReason.OTHER -> SwapStatusFailureReasonResponse.OTHER
            SwapStatusFailureReason.USER_CANCELLED -> SwapStatusFailureReasonResponse.USER_CANCELLED
            SwapStatusFailureReason.INVALID_SUBMISSION -> SwapStatusFailureReasonResponse.INVALID_SUBMISSION
            SwapStatusFailureReason.BLOCKCHAIN_ERROR -> SwapStatusFailureReasonResponse.BLOCKCHAIN_ERROR
        }
    }
}
