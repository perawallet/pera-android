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

import com.algorand.android.swap.domain.tracking.usecase.LogSwapTransactionFailureEvent
import com.algorand.android.swap.domain.tracking.usecase.LogSwapTransactionSuccessEvent
import com.algorand.android.swap.domain.usecase.RecordSwapQuoteException
import com.algorand.android.swapui.txnstatus.mapper.CompletedSwapTransactionStatusPreviewMapper
import com.algorand.android.swapui.txnstatus.mapper.FailedSwapTransactionStatusPreviewMapper
import com.algorand.android.swapui.txnstatus.mapper.SendingSwapTransactionStatusPreviewMapper
import com.algorand.android.swapui.txnstatus.model.SwapTransactionStatusNavArgs
import com.algorand.android.swapui.txnstatus.model.SwapTransactionStatusPreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetSwapTransactionStatusPreviewFlowUseCase @Inject constructor(
    private val sendSignedSwapTransactions: SendSignedSwapTransactions,
    private val sendingStatusPreviewMapper: SendingSwapTransactionStatusPreviewMapper,
    private val completedStatusPreviewMapper: CompletedSwapTransactionStatusPreviewMapper,
    private val failedStatusPreviewMapper: FailedSwapTransactionStatusPreviewMapper,
    private val logSwapTransactionSuccessEvent: LogSwapTransactionSuccessEvent,
    private val logSwapTransactionFailureEvent: LogSwapTransactionFailureEvent,
    private val recordSwapQuoteException: RecordSwapQuoteException
) : GetSwapTransactionStatusPreviewFlow {

    override suspend fun invoke(args: SwapTransactionStatusNavArgs): Flow<SwapTransactionStatusPreview> = flow {
        emit(sendingStatusPreviewMapper(args.swapQuote))
        sendSignedSwapTransactions(args.signedSwapTransactions.toList()).use(
            onSuccess = {
                emit(completedStatusPreviewMapper(args))
                logSwapTransactionSuccessEvent(args.swapQuote, args.networkFeesAsAlgo)
            },
            onFailed = { exception, _ ->
                emit(failedStatusPreviewMapper(args.swapQuote))
                logSwapTransactionFailureEvent(args.swapQuote)
                recordSwapQuoteException(args.swapQuote.quoteId, exception.message)
            }
        )
    }
}
