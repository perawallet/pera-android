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

import com.algorand.wallet.swap.data.model.SwapHistoryStatusResponse
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus
import javax.inject.Inject

internal class DefaultSwapHistoryStatusMapper @Inject constructor() : SwapHistoryStatusMapper {

    override fun invoke(response: SwapHistoryStatusResponse): SwapHistoryStatus {
        return when (response) {
            SwapHistoryStatusResponse.PENDING -> SwapHistoryStatus.Pending
            SwapHistoryStatusResponse.COMPLETED -> SwapHistoryStatus.Completed
            SwapHistoryStatusResponse.FAILED -> SwapHistoryStatus.Failed
            SwapHistoryStatusResponse.IN_PROGRESS -> SwapHistoryStatus.InProgress
        }
    }

    override fun invoke(status: SwapHistoryStatus): SwapHistoryStatusResponse {
        return when (status) {
            SwapHistoryStatus.Pending -> SwapHistoryStatusResponse.PENDING
            SwapHistoryStatus.Completed -> SwapHistoryStatusResponse.COMPLETED
            SwapHistoryStatus.Failed -> SwapHistoryStatusResponse.FAILED
            SwapHistoryStatus.InProgress -> SwapHistoryStatusResponse.IN_PROGRESS
        }
    }
}
