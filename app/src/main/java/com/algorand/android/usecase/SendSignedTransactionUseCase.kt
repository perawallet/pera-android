/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.usecase

import com.algorand.android.models.Account
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.SignedTransactionDetail.AssetOperation.AssetAddition
import com.algorand.android.models.SignedTransactionDetail.Send
import com.algorand.android.models.TrackTransactionRequest
import com.algorand.android.modules.transaction.confirmation.domain.usecase.TransactionConfirmationUseCase
import com.algorand.android.node.domain.usecase.IsSelectedNodeMainnet
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.analytics.logTransactionEvent
import com.algorand.android.utils.exception.AccountAlreadyOptedIntoAssetException
import com.algorand.android.utils.exception.AssetAlreadyPendingForRemovalException
import com.algorand.android.utils.exceptions.TransactionConfirmationAwaitException
import com.algorand.android.utils.exceptions.TransactionIdNullException
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class SendSignedTransactionUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val transactionConfirmationUseCase: TransactionConfirmationUseCase,
    private val isSelectedNodeMainnet: IsSelectedNodeMainnet
) {

    suspend fun sendSignedTransaction(
        signedTransactionDetail: SignedTransactionDetail,
        shouldLogTransaction: Boolean = true
    ) = channelFlow<DataResource<String>> {
        send(DataResource.Loading())
        if (signedTransactionDetail is AssetAddition && isAccountAlreadyOptedIntoAsset(signedTransactionDetail)) {
            send(DataResource.Error.Local(AccountAlreadyOptedIntoAssetException()))
        } else if (signedTransactionDetail is SignedTransactionDetail.AssetOperation.AssetRemoval &&
            isAssetPendingForRemovalFromAccount(signedTransactionDetail)
        ) {
            send(DataResource.Error.Local(AssetAlreadyPendingForRemovalException()))
        } else {
            transactionsRepository.sendSignedTransaction(signedTransactionDetail.signedTransactionData).use(
                onSuccess = { sendTransactionResponse ->
                    val txnId = sendTransactionResponse.taxId
                    if (signedTransactionDetail.shouldWaitForConfirmation) {
                        if (txnId.isNullOrBlank()) {
                            send(DataResource.Error.Local(TransactionIdNullException()))
                            return@use
                        }
                        transactionConfirmationUseCase.waitForConfirmation(txnId).collectLatest {
                            it.useSuspended(
                                onSuccess = {
                                    send(getSendTransactionResult(signedTransactionDetail, shouldLogTransaction, txnId))
                                },
                                onFailed = { error ->
                                    error.exception?.let { exception ->
                                        send(DataResource.Error.Api(exception, error.code))
                                    } ?: send(DataResource.Error.Api(TransactionConfirmationAwaitException(), null))
                                }
                            )
                        }
                    } else {
                        send(getSendTransactionResult(signedTransactionDetail, shouldLogTransaction, txnId))
                    }
                },
                onFailed = { exception, code ->
                    send(DataResource.Error.Api(exception, code))
                }
            )
        }
    }

    private suspend fun getSendTransactionResult(
        signedTransactionDetail: SignedTransactionDetail,
        shouldLogTransaction: Boolean,
        txnId: String?
    ): DataResource<String> {
        txnId?.let { transactionId ->
            transactionsRepository.postTrackTransaction(TrackTransactionRequest(transactionId))
            if (shouldLogTransaction && signedTransactionDetail is Send) {
                logTransactionEvent(signedTransactionDetail, transactionId)
            }
        }
        cacheAssetIfAssetOperationTransaction(signedTransactionDetail)
        return DataResource.Success(txnId.orEmpty())
    }

    private fun logTransactionEvent(signedTransactionDetail: Send, taxId: String?) {
        if (isSelectedNodeMainnet()) {
            with(signedTransactionDetail) {
//                firebaseAnalytics.logTransactionEvent(
//                    amount = amount,
//                    assetId = assetInformation.assetId,
//                    accountType = senderAccountType ?: Account.Type.STANDARD,
//                    isMax = isMax,
//                    transactionId = taxId
//                ) TODO
            }
        }
    }

    private fun isAccountAlreadyOptedIntoAsset(transaction: AssetAddition): Boolean {
        return false
//        return accountDetailUseCase.isAssetOwnedByAccount(
//            publicKey = transaction.senderAccountAddress,
//            assetId = transaction.assetInformation.assetId
//        )
    }

    private fun isAssetPendingForRemovalFromAccount(
        transaction: SignedTransactionDetail.AssetOperation.AssetRemoval
    ): Boolean {
        return false
//        return accountDetailUseCase.isAssetPendingForRemovalFromAccount(
//            accountAddress = transaction.senderAccountAddress,
//            assetId = transaction.assetInformation.assetId
//        )
        // TODO
    }

    private suspend fun cacheAssetIfAssetOperationTransaction(signedTransactionDetail: SignedTransactionDetail) {
        when (signedTransactionDetail) {
            is AssetAddition -> {
//                assetAdditionUseCase.addAssetAdditionToAccountCache(
//                    publicKey = signedTransactionDetail.senderAccountAddress,
//                    assetInformation = signedTransactionDetail.assetInformation
//                )
                // TODO implement AddAssetAdditionToAccountAssetHoldings
            }
            is SignedTransactionDetail.AssetOperation.AssetRemoval -> {
//                accountAssetRemovalUseCase.addAssetDeletionToAccountCache(
//                    publicKey = signedTransactionDetail.senderAccountAddress,
//                    assetId = signedTransactionDetail.assetInformation.assetId
//                )
                // TODO implement AddAssetRemovalToAccountAssetHoldings
            }
            else -> Unit
        }
    }
}
