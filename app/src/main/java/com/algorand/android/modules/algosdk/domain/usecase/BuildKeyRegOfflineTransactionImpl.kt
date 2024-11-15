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
import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.algosdk.domain.mapper.TransactionParametersResponseMapper
import javax.inject.Inject

internal class BuildKeyRegOfflineTransactionImpl @Inject constructor(
    private val transactionParametersResponseMapper: TransactionParametersResponseMapper
) : BuildKeyRegOfflineTransaction {

    override fun invoke(address: String, txnParams: TransactionParams): ByteArray? {
        return try {
            createTransaction(address, txnParams)
        } catch (e: Exception) {
            null
        }
    }

    private fun createTransaction(address: String, txnParams: TransactionParams): ByteArray {
        val params = transactionParametersResponseMapper(txnParams)
        return KeyRegistrationTransactionBuilder.Builder()
            .suggestedParams(params)
            .sender(address)
            .build()
            .bytes()
    }
}
