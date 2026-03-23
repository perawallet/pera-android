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

internal class SwapSentAmountValidationRule @Inject constructor(
    private val getParsedSwapTransactions: GetParsedSwapTransactions
) : SwapTransactionValidationRule {

    override fun invoke(data: SwapTransactionValidationData): Boolean {
        val expectedMaxSentAmount = getExpectedMaxSentAmount(data.quote)
        val transactions = getParsedSwapTransactions(data.signedTransactions, data.unsignedTransactions)
        val actualSentAmount = getSentAmount(data.quote, transactions)
        return actualSentAmount <= expectedMaxSentAmount
    }

    private fun getExpectedMaxSentAmount(quote: SwapQuoteV2): BigInteger {
        val baseAmount = quote.assetInAmount.amountWithSlippage
            .movePointRight(quote.assetInDetail.fractionDecimals)
            .toBigInteger()
        
        return if (quote.isAssetInAlgo) {
            val peraFeeInAlgo = quote.fee.peraFeeAmountInAlgo
                .movePointRight(quote.assetInDetail.fractionDecimals)
                .toBigInteger()
            baseAmount.add(peraFeeInAlgo)
        } else {
            baseAmount
        }
    }

    private fun getSentAmount(quote: SwapQuoteV2, transactions: List<RawTransaction>): BigInteger {
        return if (quote.isAssetInAlgo) {
            getSentAlgoAmount(transactions, quote.accountAddress)
        } else {
            getSentAssetAmount(transactions, quote.accountAddress, quote.assetInDetail.assetId)
        }
    }

    private fun getSentAlgoAmount(txns: List<RawTransaction>, accountAddress: String): BigInteger {
        var totalSent = BigInteger.ZERO
        txns.forEach { txn ->
            val isPaymentTxn = txn.transactionType == RawTransactionType.PAY_TRANSACTION
            val isSenderMatching = txn.senderAddress?.decodedAddress == accountAddress
            if (isPaymentTxn && isSenderMatching) {
                val algoAmount = txn.amount?.toBigIntegerOrNull() ?: BigInteger.ZERO
                totalSent = totalSent.add(algoAmount)
            }
        }
        return totalSent
    }

    private fun getSentAssetAmount(txns: List<RawTransaction>, accountAddress: String, assetId: Long): BigInteger {
        var totalSent = BigInteger.ZERO
        txns.forEach { txn ->
            val isAssetTransferTxn = txn.transactionType == RawTransactionType.ASSET_TRANSACTION
            val isSenderMatching = txn.senderAddress?.decodedAddress == accountAddress
            val isAssetIdMatching = txn.assetId == assetId
            if (isAssetTransferTxn && isSenderMatching && isAssetIdMatching) {
                val assetAmount = txn.assetAmount ?: BigInteger.ZERO
                totalSent = totalSent.add(assetAmount)
            }
        }
        return totalSent
    }
}
