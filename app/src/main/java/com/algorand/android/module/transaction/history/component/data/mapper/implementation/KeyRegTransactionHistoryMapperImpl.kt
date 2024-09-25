package com.algorand.android.module.transaction.history.component.data.mapper.implementation

import com.algorand.android.module.transaction.history.component.data.mapper.KeyRegTransactionHistoryMapper
import com.algorand.android.module.transaction.history.component.data.model.KeyRegTransactionHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.KeyRegTransactionHistory
import javax.inject.Inject

internal class KeyRegTransactionHistoryMapperImpl @Inject constructor() : KeyRegTransactionHistoryMapper {

    override fun invoke(response: KeyRegTransactionHistoryResponse?): KeyRegTransactionHistory? {
        if (response == null) return null
        return KeyRegTransactionHistory(
            voteKey = response.voteParticipationKey,
            selectionKey = response.selectionParticipationKey,
            stateProofKey = response.stateProofKey,
            validFirstRound = response.voteFirstValid,
            validLastRound = response.voteLastValid,
            voteKeyDilution = response.voteKeyDilution,
            nonParticipation = response.nonParticipation,
        )
    }
}
