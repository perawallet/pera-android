package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName

internal data class KeyRegTransactionHistoryResponse(
    @SerializedName("vote-participation-key")
    val voteParticipationKey: String?,
    @SerializedName("selection-participation-key")
    val selectionParticipationKey: String?,
    @SerializedName("state-proof-key")
    val stateProofKey: String?,
    @SerializedName("vote-first-valid")
    val voteFirstValid: Long?,
    @SerializedName("vote-last-valid")
    val voteLastValid: Long?,
    @SerializedName("vote-key-dilution")
    val voteKeyDilution: Long?,
    @SerializedName("non-participation")
    val nonParticipation: Boolean?
)
