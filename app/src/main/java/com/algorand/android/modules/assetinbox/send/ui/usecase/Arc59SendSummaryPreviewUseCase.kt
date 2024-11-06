/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AssetInformation.Companion.ALGO_ID
import com.algorand.android.models.BaseAssetDetail
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.assetinbox.send.domain.mapper.Arc59TransactionPayloadMapper
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendSummary
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendTransaction
import com.algorand.android.modules.assetinbox.send.domain.usecase.CreateArc59Transactions
import com.algorand.android.modules.assetinbox.send.domain.usecase.GetArc59SendSummary
import com.algorand.android.modules.assetinbox.send.ui.mapper.Arc59SendSummaryPreviewMapper
import com.algorand.android.modules.assetinbox.send.ui.model.Arc59SendSummaryNavArgs
import com.algorand.android.modules.assetinbox.send.ui.model.Arc59SendSummaryPreview
import com.algorand.android.nft.domain.usecase.SimpleCollectibleUseCase
import com.algorand.android.usecase.GetBaseOwnedAssetDataUseCase
import com.algorand.android.usecase.SimpleAssetDetailUseCase
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.isGreaterThan
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class Arc59SendSummaryPreviewUseCase @Inject constructor(
    private val getArc59SendSummary: GetArc59SendSummary,
    private val arc59SendSummaryPreviewMapper: Arc59SendSummaryPreviewMapper,
    private val simpleAssetDetailUseCase: SimpleAssetDetailUseCase,
    private val simpleCollectibleUseCase: SimpleCollectibleUseCase,
    private val arc59TransactionSendProcessor: Arc59TransactionSendProcessor,
    private val createArc59Transactions: CreateArc59Transactions,
    private val arc59TransactionPayloadMapper: Arc59TransactionPayloadMapper,
    private val getBaseOwnedAssetDataUseCase: GetBaseOwnedAssetDataUseCase
) {

    fun getInitialPreview(): Arc59SendSummaryPreview {
        return arc59SendSummaryPreviewMapper.getInitialPreview()
    }

    fun getArc59SendSummaryPreview(
        preview: Arc59SendSummaryPreview,
        receiverAccountAddress: String,
        assetId: Long,
        amount: BigInteger
    ): Flow<Arc59SendSummaryPreview> = flow {
        val assetDetail = getAssetOrCollectibleDetailOrNull(assetId)
        if (assetDetail == null) {
            val errorEvent = Event(ErrorResource.LocalErrorResource.Local(R.string.an_error_occured))
            val newPreview = preview.copy(isLoading = false, showError = errorEvent)
            emit(newPreview)
            return@flow
        }
        val sendSummary = getArc59SendSummary(receiverAccountAddress, assetId)
        sendSummary.use(
            onSuccess = {
                emit(createArc59SendSummaryPreview(it, amount, assetDetail))
            },
            onFailed = { exception, _ ->
                val errorEvent = Event(ErrorResource.Api(exception.message.orEmpty()))
                val newPreview = preview.copy(isLoading = false, showError = errorEvent)
                emit(newPreview)
            }
        )
    }

    fun createArc59SendTransactionData(
        args: Arc59SendSummaryNavArgs,
        preview: Arc59SendSummaryPreview
    ): Flow<Arc59SendSummaryPreview> = flow {
        if (preview.summary == null) {
            val errorEvent = Event(ErrorResource.LocalErrorResource.Local(R.string.an_error_occured))
            emit(preview.copy(isLoading = false, showError = errorEvent))
        } else {
            if (!hasAccountEnoughAlgo(args.senderPublicKey, preview.summary.totalProtocolAndMbrFee)) {
                val errorEvent = Event(ErrorResource.LocalErrorResource.Local(R.string.this_account_doesn_t))
                emit(preview.copy(isLoading = false, showError = errorEvent))
                return@flow
            }
            emit(preview.copy(isLoading = true))
            val txnResultFlow = createArc59Transactions(arc59TransactionPayloadMapper(args, preview.summary)).map {
                preview.getArc59SendTransactionDataPreview(it)
            }
            emitAll(txnResultFlow)
        }
    }

    private fun hasAccountEnoughAlgo(address: String, minimumBalance: BigInteger): Boolean {
        val accountAlgoAssetData = getBaseOwnedAssetDataUseCase.getBaseOwnedAssetData(ALGO_ID, address)
        val safeAlgoAsset = accountAlgoAssetData?.amount ?: BigInteger.ZERO
        return safeAlgoAsset isGreaterThan minimumBalance
    }

    private fun Arc59SendSummaryPreview.getArc59SendTransactionDataPreview(
        transactions: Result<List<Arc59SendTransaction>>
    ): Arc59SendSummaryPreview {
        return if (transactions.isSuccess && transactions.getOrNull() != null) {
            copy(arc59Transactions = Event(transactions.getOrNull()), isLoading = false, showError = null)
        } else {
            val errorEvent = Event(ErrorResource.Api(transactions.exceptionOrNull()?.message.orEmpty()))
            copy(isLoading = false, showError = errorEvent)
        }
    }

    suspend fun sendSignedTransaction(
        preview: Arc59SendSummaryPreview,
        signedTransactions: List<Any?>
    ): Flow<Arc59SendSummaryPreview> = channelFlow {
        val safeSignedTransactions = signedTransactions.filterIsInstance<SignedTransactionDetail>()
        if (safeSignedTransactions.isEmpty()) {
            val errorEvent = Event(ErrorResource.LocalErrorResource.Local(R.string.an_error_occured))
            send(preview.copy(isLoading = false, showError = errorEvent))
            return@channelFlow
        }

        arc59TransactionSendProcessor.sendSignedTransactions(
            safeSignedTransactions.toMutableList(),
            onSendTransactionsSuccess = { transactionId ->
                send(preview.copy(isLoading = false, onTxnSendSuccessfully = Event(transactionId)))
            },
            onSendTransactionsFailed = { errorMessage ->
                errorMessage?.let {
                    send(preview.copy(isLoading = false, showError = Event(ErrorResource.Api(it))))
                }
            }
        )
    }

    private fun createArc59SendSummaryPreview(
        arc59SendSummary: Arc59SendSummary,
        amount: BigInteger,
        assetDetail: BaseAssetDetail
    ): Arc59SendSummaryPreview {
        return arc59SendSummaryPreviewMapper(
            arc59SendSummary,
            amount,
            assetDetail,
            isLoading = false,
            showError = null,
            onNavBack = null,
            arc59Transactions = null
        )
    }

    private fun getAssetOrCollectibleDetailOrNull(assetId: Long): BaseAssetDetail? {
        return simpleAssetDetailUseCase.getCachedAssetDetail(assetId)?.data
            ?: simpleCollectibleUseCase.getCachedCollectibleById(assetId)?.data
    }
}
