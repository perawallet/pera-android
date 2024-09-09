package com.algorand.android.accountinfo.component.data.model

import com.google.gson.annotations.SerializedName

internal data class AccountInformationResponse(
    @SerializedName("account")
    val accountInformation: AccountInformationResponsePayloadResponse?,

    @SerializedName("current-round")
    val currentRound: Long?
)
