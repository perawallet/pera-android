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

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui.domain

import com.algorand.android.R
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.mapper.Arc59ClaimTransactionPayloadMapper
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.mapper.Arc59RejectTransactionPayloadMapper
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase.CreateArc59ClaimTransaction
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase.CreateArc59RejectTransaction
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.mapper.Arc59ReceiveDetailPreviewMapper
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailPreview
import com.algorand.android.usecase.SendSignedTransactionUseCase
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

class Arc59ReceiveDetailPreviewUseCase @Inject constructor(
    private val createArc59ClaimTransaction: CreateArc59ClaimTransaction,
    private val createArc59RejectTransaction: CreateArc59RejectTransaction,
    private val arc59ClaimTransactionPayloadMapper: Arc59ClaimTransactionPayloadMapper,
    private val arc59RejectTransactionPayloadMapper: Arc59RejectTransactionPayloadMapper,
    private val arc59ReceiveDetailPreviewMapper: Arc59ReceiveDetailPreviewMapper,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase
) {

    fun getInitialPreview(args: Arc59ReceiveDetailNavArgs): Arc59ReceiveDetailPreview {
        return arc59ReceiveDetailPreviewMapper.getInitialPreview(args)
    }

    fun createRejectTransaction(
        args: Arc59ReceiveDetailNavArgs,
        preview: Arc59ReceiveDetailPreview
    ): Flow<Arc59ReceiveDetailPreview> = flow {
        emit(preview.copy(isLoading = true))
        val payload = arc59RejectTransactionPayloadMapper(args)
        createArc59RejectTransaction(payload).use(
            onSuccess = {
                emit(preview.copy(rejectTransaction = Event(it), isLoading = false))
            },
            onFailed = { exception, _ ->
                val errorEvent = Event(ErrorResource.Api(exception.message.orEmpty()))
                emit(preview.copy(showError = errorEvent, isLoading = false))
            }
        )
    }

    fun createClaimTransaction(
        args: Arc59ReceiveDetailNavArgs,
        preview: Arc59ReceiveDetailPreview
    ): Flow<Arc59ReceiveDetailPreview> = flow {
        emit(preview.copy(isLoading = true))
        val payload = arc59ClaimTransactionPayloadMapper(args)
        createArc59ClaimTransaction(payload).use(
            onSuccess = {
                emit(preview.copy(claimTransaction = Event(it), isLoading = false))
            },
            onFailed = { exception, _ ->
                emit(preview.copy(showError = Event(ErrorResource.Api(exception.message.orEmpty())), isLoading = false))
            }
        )
    }

    fun sendSignedTransaction(
        signedTransactions: List<Any?>,
        preview: Arc59ReceiveDetailPreview
    ): Flow<Arc59ReceiveDetailPreview> = channelFlow {
        send(preview.copy(isLoading = true))
        val safeSignedTransactions = signedTransactions.filterIsInstance<SignedTransactionDetail>()
        if (safeSignedTransactions.isEmpty()) {
            val errorEvent = Event(ErrorResource.LocalErrorResource.Local(R.string.an_error_occured))
            send(preview.copy(isLoading = false, showError = errorEvent))
            return@channelFlow
        }

        sendSignedTransactionUseCase.sendSignedTransaction(safeSignedTransactions.first()).collectLatest {
            it.useSuspended(
                onSuccess = { transactionId ->
                    send(preview.copy(onTransactionSendSuccessfully = Event(transactionId), isLoading = false))
                },
                onFailed = {
                    val errorEvent = Event(ErrorResource.Api(it.exception?.message.orEmpty()))
                    send(preview.copy(showError = errorEvent, isLoading = false))
                }
            )
        }
    }
}
