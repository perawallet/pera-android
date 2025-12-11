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

package com.algorand.wallet.swap.domain.validation.rules

import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.usecase.GetParsedSwapTransactions
import com.algorand.wallet.swap.domain.validation.SwapTransactionValidationRule
import com.algorand.wallet.swap.domain.validation.model.SwapTransactionValidationData
import java.math.BigInteger
import javax.inject.Inject

internal class SwapReceivedAmountValidationRule @Inject constructor(
    private val getParsedSwapTransactions: GetParsedSwapTransactions
) : SwapTransactionValidationRule {

    override fun invoke(data: SwapTransactionValidationData): Boolean {
        val expectedMinReceivedAmount = getExpectedMinReceivedAmount(data.quote)
        val transactions = getParsedSwapTransactions(data.signedTransactions, data.unsignedTransactions)
        val actualReceivedAmount = getReceivedAmount(data.quote, transactions)
        return actualReceivedAmount >= expectedMinReceivedAmount
    }

    private fun getExpectedMinReceivedAmount(quote: SwapQuoteV2): BigInteger {
        return quote.assetOutAmount.amountWithSlippage
            .movePointRight(quote.assetOutDetail.fractionDecimals)
            .toBigInteger()
    }

    private fun getReceivedAmount(quote: SwapQuoteV2, transactions: List<RawTransaction>): BigInteger {
        return if (quote.isAssetOutAlgo) {
            getReceivedAlgoAmount(transactions, quote.accountAddress)
        } else {
            getReceivedAssetAmount(transactions, quote.accountAddress, quote.assetOutDetail.assetId)
        }
    }

    private fun getReceivedAlgoAmount(txns: List<RawTransaction>, accountAddress: String): BigInteger {
        var totalReceived = BigInteger.ZERO
        txns.forEach { txn ->
            val isPaymentTxn = txn.transactionType == RawTransactionType.PAY_TRANSACTION
            val isReceiverMatching = txn.receiverAddress?.decodedAddress == accountAddress
            if (isPaymentTxn && isReceiverMatching) {
                val algoAmount = txn.amount?.toBigIntegerOrNull() ?: BigInteger.ZERO
                totalReceived = totalReceived.add(algoAmount)
            }
        }
        return totalReceived
    }

    private fun getReceivedAssetAmount(txns: List<RawTransaction>, accountAddress: String, assetId: Long): BigInteger {
        var totalReceived = BigInteger.ZERO
        txns.forEach { txn ->
            val isAssetTransferTxn = txn.transactionType == RawTransactionType.ASSET_TRANSACTION
            val isReceiverMatching = txn.assetReceiverAddress?.decodedAddress == accountAddress
            val isAssetIdMatching = txn.assetId == assetId
            if (isAssetTransferTxn && isReceiverMatching && isAssetIdMatching) {
                val assetAmount = txn.assetAmount ?: BigInteger.ZERO
                totalReceived = totalReceived.add(assetAmount)
            }
        }
        return totalReceived
    }
}
