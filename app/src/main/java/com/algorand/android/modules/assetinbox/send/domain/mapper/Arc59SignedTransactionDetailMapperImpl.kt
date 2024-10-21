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
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59Transactions
import com.algorand.android.utils.flatten
import javax.inject.Inject

class Arc59SignedTransactionDetailMapperImpl @Inject constructor() : Arc59SignedTransactionDetailMapper {

    override fun invoke(
        transactions: Arc59Transactions,
        signedTransactions: List<ByteArray?>?
    ): MutableList<SignedTransactionDetail>? {
        if (signedTransactions == null || !areAllTxnsSigned(transactions, signedTransactions)) return null

        val optInTransactions = getOptInTransactions(transactions, signedTransactions)
        val sendTransactions = getSendTransactions(transactions, signedTransactions) ?: return null

        if (transactions.optInTransactions.isNotEmpty() && optInTransactions == null) return null

        return mutableListOf<SignedTransactionDetail>().apply {
            optInTransactions?.let { add(it) }
            add(sendTransactions)
        }
    }

    private fun getOptInTransactions(
        transactions: Arc59Transactions,
        signedTransactions: List<ByteArray?>
    ): SignedTransactionDetail? {
        val optInSize = transactions.optInTransactions.size
        if (optInSize == 0) return null

        val signedOptInTransactions = signedTransactions
            .take(optInSize)
            .filterNotNull()
            .flatten()

        return SignedTransactionDetail.Arc59OptIn(signedOptInTransactions)
    }

    private fun getSendTransactions(
        transactions: Arc59Transactions,
        signedTransactions: List<ByteArray?>
    ): SignedTransactionDetail {
        val startIndex = transactions.optInTransactions.size
        val signedSendTransactionsFiltered = signedTransactions
            .drop(startIndex)
            .filterNotNull()
            .flatten()

        return SignedTransactionDetail.Arc59Send(signedSendTransactionsFiltered)
    }

    private fun areAllTxnsSigned(transactions: Arc59Transactions, signedTransactions: List<Any?>): Boolean {
        return signedTransactions.size == (transactions.optInTransactions.size + transactions.sendTransactions.size)
    }
}
