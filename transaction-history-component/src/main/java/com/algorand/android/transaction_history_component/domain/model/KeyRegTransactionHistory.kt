package com.algorand.android.transaction_history_component.domain.model

data class KeyRegTransactionHistory(
    val voteKey: String?,
    val selectionKey: String?,
    val stateProofKey: String?,
    val validFirstRound: Long?,
    val validLastRound: Long?,
    val voteKeyDilution: Long?,
    val nonParticipation: Boolean?
)
