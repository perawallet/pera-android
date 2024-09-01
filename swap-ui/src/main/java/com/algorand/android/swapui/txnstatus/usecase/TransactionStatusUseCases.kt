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

package com.algorand.android.swapui.txnstatus.usecase

import com.algorand.android.foundation.PeraResult
import com.algorand.android.swapui.txnstatus.model.SignedSwapTransaction
import com.algorand.android.swapui.txnstatus.model.SwapTransactionStatusNavArgs
import com.algorand.android.swapui.txnstatus.model.SwapTransactionStatusPreview
import kotlinx.coroutines.flow.Flow

interface GetSwapTransactionStatusPreviewFlow {
    suspend operator fun invoke(args: SwapTransactionStatusNavArgs): Flow<SwapTransactionStatusPreview>
}

internal interface SendSignedSwapTransactions {
    suspend operator fun invoke(signedTransactions: List<SignedSwapTransaction>): PeraResult<Unit>
}