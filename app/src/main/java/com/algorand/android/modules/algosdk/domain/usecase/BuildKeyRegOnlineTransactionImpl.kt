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

package com.algorand.android.modules.algosdk.domain.usecase

import com.algorand.algosdk.builder.transaction.KeyRegistrationTransactionBuilder
import com.algorand.android.modules.algosdk.domain.mapper.TransactionParametersResponseMapper
import com.algorand.android.modules.algosdk.domain.model.OnlineKeyRegTransactionPayload
import javax.inject.Inject

internal class BuildKeyRegOnlineTransactionImpl @Inject constructor(
    private val transactionParametersResponseMapper: TransactionParametersResponseMapper
) : BuildKeyRegOnlineTransaction {

    override fun invoke(params: OnlineKeyRegTransactionPayload): ByteArray? {
        return try {
            createTransaction(params)
        } catch (exception: Exception) {
            null
        }
    }

    private fun createTransaction(params: OnlineKeyRegTransactionPayload): ByteArray {
        return with(params) {
            val txnParamsResponse = transactionParametersResponseMapper(params.txnParams)
            KeyRegistrationTransactionBuilder.Builder()
                .suggestedParams(txnParamsResponse)
                .sender(senderAddress)
                .selectionPublicKeyBase64(selectionPublicKey)
                .participationPublicKeyBase64(voteKey)
                .voteFirst(params.voteFirstRound.toLong())
                .voteLast(params.voteLastRound.toLong())
                .voteKeyDilution(params.voteKeyDilution.toLong())
                .build()
                .bytesToSign()
        }
    }
}
