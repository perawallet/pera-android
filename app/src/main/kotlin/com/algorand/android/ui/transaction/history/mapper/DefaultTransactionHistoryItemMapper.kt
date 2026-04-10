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

package com.algorand.android.ui.transaction.history.mapper

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.ApplicationCall
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetConfiguration
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.Heartbeat
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.KeyRegistration
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.Payment
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.Payment.PaymentType
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.Swap
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultTransactionHistoryItemMapper @Inject constructor() : TransactionHistoryItemMapper {

    override fun map(transactionHistory: TransactionHistory): TransactionHistoryItem {
        return with(transactionHistory) {
            when (this.type) {
                is ApplicationCall -> mapApplicationCallTransaction(transactionHistory)
                is AssetConfiguration -> TransactionHistoryItem.AssetConfiguration(id, fee.formatAsFee())
                Heartbeat -> TransactionHistoryItem.Heartbeat(id, fee.formatAsFee())
                KeyRegistration -> TransactionHistoryItem.KeyRegistration(id, fee.formatAsFee())
                is AssetTransfer -> mapAssetTransferTransaction(transactionHistory)
                is Payment -> mapPaymentTransaction(transactionHistory)
                is Swap -> mapSwapTransaction(transactionHistory)
            }
        }
    }

    private fun mapPaymentTransaction(transactionHistory: TransactionHistory): TransactionHistoryItem {
        val payment = transactionHistory.type as Payment
        return with(transactionHistory) {
            when (payment.type) {
                is PaymentType.Receive -> {
                    val formattedAmount = formatAmount(ALGO_ID, payment.amount, Currency.ALGO.symbol)
                    TransactionHistoryItem.Receive(id, payment.amount, senderAddress, formattedAmount)
                }

                PaymentType.Self -> {
                    val formattedAmount = formatAmount(ALGO_ID, payment.amount, Currency.ALGO.symbol)
                    TransactionHistoryItem.Self(id, formattedAmount)
                }

                is PaymentType.Send -> {
                    val formattedAmount = formatSendAmount(ALGO_ID, payment.amount, Currency.ALGO.symbol)
                    val receiverAddress = (payment.type as PaymentType.Send).receiverAddress
                    TransactionHistoryItem.Send(id, payment.amount, receiverAddress, formattedAmount)
                }
            }
        }
    }

    private fun mapAssetTransferTransaction(transactionHistory: TransactionHistory): TransactionHistoryItem {
        val assetTransfer = transactionHistory.type as AssetTransfer
        val assetTransferType = (transactionHistory.type as AssetTransfer).type
        val assetId = assetTransfer.assetId
        val unitName = assetTransfer.assetUnitName
        val fee = transactionHistory.fee.formatAsFee()
        val txId = transactionHistory.id
        return when (assetTransferType) {
            AssetTransferType.OptIn -> TransactionHistoryItem.OptIn(txId, fee)
            AssetTransferType.OptOut -> TransactionHistoryItem.OptOut(txId, fee)
            is AssetTransferType.SendOptOut -> {
                val sendOptOut = assetTransfer.type as AssetTransferType.SendOptOut
                val formattedAmount = formatSendAmount(assetId, sendOptOut.amount, unitName)
                TransactionHistoryItem.SendOptOut(txId, formattedAmount)
            }

            is AssetTransferType.ReceiveOptOut -> {
                val receiveOptOut = assetTransfer.type as AssetTransferType.ReceiveOptOut
                val formattedAmount = formatAmount(assetId, receiveOptOut.amount, unitName)
                TransactionHistoryItem.ReceiveOptOut(transactionHistory.id, formattedAmount)
            }

            is AssetTransferType.Receive -> {
                TransactionHistoryItem.Receive(
                    id = transactionHistory.id,
                    amount = assetTransferType.amount,
                    senderAddress = transactionHistory.senderAddress,
                    formattedAmount = formatAmount(assetId, assetTransferType.amount, unitName)
                )
            }

            is AssetTransferType.Self -> {
                TransactionHistoryItem.Self(txId, formatAmount(assetId, assetTransferType.amount, unitName))
            }

            is AssetTransferType.Send -> {
                TransactionHistoryItem.Send(
                    id = transactionHistory.id,
                    amount = assetTransferType.amount,
                    receiverAddress = assetTransferType.receiverAddress,
                    formattedAmount = formatSendAmount(assetId, assetTransferType.amount, unitName)
                )
            }
        }
    }

    private fun mapSwapTransaction(transactionHistory: TransactionHistory): TransactionHistoryItem {
        val swapDetails = transactionHistory.type as Swap
        return with(swapDetails) {
            TransactionHistoryItem.Swap(
                id = transactionHistory.id,
                groupId = swapDetails.groupId,
                formattedAmountIn = formatAmount(assetInId, amountIn, assetInUnitName),
                formattedAmountOut = formatAmount(assetOutId, amountOut, assetOutUnitName)
            )
        }
    }

    private fun mapApplicationCallTransaction(transactionHistory: TransactionHistory): TransactionHistoryItem {
        val applicationCall = transactionHistory.type as ApplicationCall
        return TransactionHistoryItem.ApplicationCall(
            id = transactionHistory.id,
            appId = applicationCall.applicationId.toString(),
            txnCount = applicationCall.txnCount,
            formattedFee = transactionHistory.fee.formatAsFee(),
            balanceImpactAmount = applicationCall.amount,
            formattedBalanceImpact = formatBalanceImpact(applicationCall)
        )
    }

    private fun formatBalanceImpact(applicationCall: ApplicationCall): String? {
        val amount = applicationCall.amount ?: return null
        val absoluteAmount = amount.abs().toPlainString()
        val sign = if (amount < BigDecimal.ZERO) "-" else ""
        return if (applicationCall.assetId == ALGO_ID) {
            "$sign${Currency.ALGO.symbol}$absoluteAmount"
        } else {
            "$sign$absoluteAmount ${applicationCall.assetUnitName.orEmpty()}"
        }
    }

    private fun BigDecimal.formatAsFee(): String {
        return StringBuilder().apply {
            if (this@formatAsFee > BigDecimal.ZERO) append("-")
            append(Currency.ALGO.symbol)
            append(toPlainString())
        }.toString()
    }

    private fun formatAmount(assetId: Long, amount: BigDecimal, assetUnitName: String): String {
        return StringBuilder().apply {
            if (assetId == ALGO_ID) {
                append(Currency.ALGO.symbol)
                append(amount.toPlainString())
            } else {
                append(amount.toPlainString())
                append(" ")
                append(assetUnitName)
            }
        }.toString()
    }

    private fun formatSendAmount(assetId: Long, amount: BigDecimal, assetUnitName: String): String {
        val formattedAmount = formatAmount(assetId, amount, assetUnitName)
        return if (amount > BigDecimal.ZERO) {
            "-$formattedAmount"
        } else {
            formattedAmount
        }
    }
}
