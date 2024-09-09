package com.algorand.android.accountinfo.component.data.model

import com.google.gson.annotations.SerializedName

internal data class ParticipationResponse(
    @SerializedName("vote-participation-key")
    val voteParticipationKey: String? = DEFAULT_PARTICIPATION_KEY
) {
    companion object {
        const val DEFAULT_PARTICIPATION_KEY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    }
}
