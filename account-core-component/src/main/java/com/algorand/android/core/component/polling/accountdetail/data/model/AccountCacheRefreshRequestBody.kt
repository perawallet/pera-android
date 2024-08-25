package com.algorand.android.core.component.polling.accountdetail.data.model

import com.google.gson.annotations.SerializedName

internal data class AccountCacheRefreshRequestBody(
    @SerializedName("account_addresses") val accountAddresses: List<String>,
    @SerializedName("last_known_round") val lastKnownRound: Long?
)
