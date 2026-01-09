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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.usecase.ParseSignedTransactionMessagePack
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import javax.inject.Inject

internal class GetParsedSwapTransactionsUseCase @Inject constructor(
    private val parseSignedTransactionMessagePack: ParseSignedTransactionMessagePack,
    private val parsedTransactionMessagePack: ParseTransactionMessagePack
) : GetParsedSwapTransactions {

    override fun invoke(
        signedTxns: List<ByteArray?>,
        unsignedTxns: List<ByteArray?>
    ): List<RawTransaction> {
        val signedTransactions = parseSignedTxns(signedTxns)
        val unsignedTransactions = parseUnsignedTxns(unsignedTxns)
        return flattenInnerTransactions(signedTransactions) + flattenInnerTransactions(unsignedTransactions)
    }

    private fun parseSignedTxns(txns: List<ByteArray?>): List<RawTransaction> {
        return txns.mapNotNull { txnBytes ->
            if (txnBytes?.isNotEmpty() == true) parseSignedTransactionMessagePack(txnBytes) else null
        }
    }

    private fun parseUnsignedTxns(txns: List<ByteArray?>): List<RawTransaction> {
        return txns.mapNotNull { txnBytes ->
            if (txnBytes?.isNotEmpty() == true) parsedTransactionMessagePack(txnBytes) else null
        }
    }

    private fun flattenInnerTransactions(transactions: List<RawTransaction>): List<RawTransaction> {
        val allTransactions = mutableListOf<RawTransaction>()
        transactions.forEach { txn ->
            allTransactions.add(txn)
            if (txn.innerTransactions?.isNotEmpty() == true) {
                allTransactions.addAll(flattenInnerTransactions(txn.innerTransactions))
            }
        }
        return allTransactions
    }
}