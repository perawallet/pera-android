package com.algorand.android.module.transaction.history.component.domain.mapper

import com.algorand.android.module.date.timeStampToZonedDateTime
import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.module.transaction.history.component.domain.model.TransactionHistory
import javax.inject.Inject

internal class KeyRegTransactionMapperImpl @Inject constructor() : KeyRegTransactionMapper {

    override fun invoke(transaction: TransactionHistory): BaseTransactionHistory {
        return if (transaction.keyRegTransaction?.voteKey != null) {
            createOnlineKeyRegTransaction(transaction)
        } else {
            createOfflineKeyRegTransaction(transaction)
        }
    }

    private fun createOnlineKeyRegTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress,
                receiverAddress = null,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.KeyReg.Online(
                    voteKey = keyRegTransaction?.voteKey.orEmpty(),
                    selectionKey = keyRegTransaction?.selectionKey.orEmpty(),
                    stateProofKey = keyRegTransaction?.stateProofKey.orEmpty(),
                    voteFirstValidRound = keyRegTransaction?.validFirstRound ?: 0,
                    voteLastValidRound = keyRegTransaction?.validLastRound ?: 0,
                    voteKeyDilution = keyRegTransaction?.voteKeyDilution ?: 0
                )
            )
        }
    }

    private fun createOfflineKeyRegTransaction(transaction: TransactionHistory): BaseTransactionHistory {
        return with(transaction) {
            BaseTransactionHistory(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress,
                receiverAddress = null,
                zonedDateTime = timeStampToZonedDateTime(roundTimeAsTimestamp ?: 0L),
                isPending = false,
                roundTimeAsTimestamp = roundTimeAsTimestamp ?: 0L,
                type = BaseTransactionHistory.BaseTransactionType.KeyReg.Offline(
                    nonParticipating = keyRegTransaction?.nonParticipation ?: false
                )
            )
        }
    }
}
