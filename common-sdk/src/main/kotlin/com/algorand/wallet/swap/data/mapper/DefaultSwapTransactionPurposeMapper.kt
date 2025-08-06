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

import com.algorand.wallet.swap.data.model.SwapTransactionPurposeResponse
import com.algorand.wallet.swap.domain.model.SwapTransactionPurpose
import javax.inject.Inject

internal class DefaultSwapTransactionPurposeMapper @Inject constructor() : SwapTransactionPurposeMapper {

    override fun invoke(response: SwapTransactionPurposeResponse?): SwapTransactionPurpose {
        if (response == null) return SwapTransactionPurpose.UNKNOWN
        return when (response) {
            SwapTransactionPurposeResponse.OPT_IN -> SwapTransactionPurpose.OPT_IN
            SwapTransactionPurposeResponse.SWAP -> SwapTransactionPurpose.SWAP
            SwapTransactionPurposeResponse.PERA_FEE -> SwapTransactionPurpose.PERA_FEE
            SwapTransactionPurposeResponse.UNKNOWN -> SwapTransactionPurpose.UNKNOWN
        }
    }
}
