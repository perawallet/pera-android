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

package com.algorand.android.modules.assetinbox.send.domain.mapper

import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendTransaction
import com.algorand.android.utils.flatten
import javax.inject.Inject

class Arc59SignedTransactionDetailMapperImpl @Inject constructor() :
    Arc59SignedTransactionDetailMapper {

    override fun invoke(
        transactions: List<Arc59SendTransaction>?,
        signedTransactions: List<ByteArray?>?
    ): List<SignedTransactionDetail>? {
        if (signedTransactions == null || !areAllTxnsSigned(transactions, signedTransactions)) {
            return null
        }

        return listOf(getSendTransactions(signedTransactions))
    }

    private fun getSendTransactions(
        signedTransactions: List<ByteArray?>
    ): SignedTransactionDetail {
        val signedSendTransactionsFiltered = signedTransactions
            .filterNotNull()
            .flatten()

        return SignedTransactionDetail.Arc59Send(signedSendTransactionsFiltered)
    }

    private fun areAllTxnsSigned(
        transactions: List<Arc59SendTransaction>?,
        signedTransactions: List<Any?>
    ): Boolean {
        return signedTransactions.size == transactions?.size
    }
}
