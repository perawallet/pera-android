package com.algorand.android.transaction_history_component.domain.mapper

import com.algorand.android.date.component.timeStampToZonedDateTime
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.transaction_history_component.domain.model.TransactionHistory
import com.algorand.android.transaction_history_component.domain.utils.TransactionUtils.isReceivingTransaction
import com.algorand.android.transaction_history_component.domain.utils.TransactionUtils.isSelfOptInTransaction
import com.algorand.android.transaction_history_component.domain.utils.TransactionUtils.isSelfTransaction
import java.math.BigInteger
import javax.inject.Inject

internal class AssetTransactionMapperImpl @Inject constructor() : AssetTransactionMapper {

    override fun invoke(transaction: TransactionHistory, publicKey: String): BaseTransactionHistory? {
        val closeToAddress = transaction.assetTransfer?.closeTo
        val receiverAddress = transaction.assetTransfer?.receiverAddress
        val senderAddress = transaction.senderAddress
        val amount = transaction.assetTransfer?.amount ?: BigInteger.ZERO
        return when {
            !closeToAddress.isNullOrBlank() && closeToAddress == publicKey -> {
                createReceiveOptOutAssetTransaction(transaction)
            }
            !closeToAddress.isNullOrBlank() && amount.compareTo(BigInteger.ZERO) == 1 -> {
                createSendOptOutAssetTransaction(transaction)
            }
            !closeToAddress.isNullOrBlank() -> createOptOutAssetTransaction(transaction)
            isSelfOptInTransaction(publicKey, senderAddress, receiverAddress, amount) -> {
                createSelfOptInAssetTransaction(transaction)
            }
            isSelfTransaction(publicKey, senderAddress, receiverAddress) -> createSelfAssetTransaction(transaction)
            isReceivingTransaction(publicKey, closeToAddress, receiverAddress) -> {
                createReceiveAssetTransfer(transaction)
            }
            else -> createSendAssetTransaction(transaction)
        }
    }

    private fun createReceiveAssetTransfer(transaction: TransactionHistory): BaseTransactionHistory? {
        val assetId = transaction.assetTransfer?.assetId ?: return null
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.BaseReceive.Receive(
                    assetId = assetId,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO
                )
            )
        }
    }

    private fun createReceiveOptOutAssetTransaction(transaction: TransactionHistory): BaseTransactionHistory? {
        val assetId = transaction.assetTransfer?.assetId ?: return null
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.BaseReceive.ReceiveOptOut(
                    assetId = assetId,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO
                )
            )
        }
    }

    private fun createSendOptOutAssetTransaction(transaction: TransactionHistory): BaseTransactionHistory? {
        val assetId = transaction.assetTransfer?.assetId ?: return null
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.BaseSend.SendOptOut(
                    assetId = assetId,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO,
                    closeToAddress = assetTransfer?.closeTo.orEmpty()
                )
            )
        }
    }

    private fun createOptOutAssetTransaction(transaction: TransactionHistory): BaseTransactionHistory? {
        val assetId = transaction.assetTransfer?.assetId ?: return null
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.OptOut(
                    assetId = assetId,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO,
                    closeToAddress = assetTransfer?.closeTo.orEmpty()
                )
            )
        }
    }

    private fun createSelfOptInAssetTransaction(transaction: TransactionHistory): BaseTransactionHistory? {
        val assetId = transaction.assetTransfer?.assetId ?: return null
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.BaseSelf.SelfOptIn(
                    assetId = assetId,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO
                )
            )
        }
    }

    private fun createSelfAssetTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.BaseSelf.Self(
                    assetId = assetTransfer?.assetId ?: 0L,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO
                )
            )
        }
    }

    private fun createSendAssetTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.AssetTransfer.BaseSend.Send(
                    assetId = assetTransfer?.assetId ?: 0L,
                    amount = assetTransfer?.amount ?: BigInteger.ZERO
                )
            )
        }
    }
}
