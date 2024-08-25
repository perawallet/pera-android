package com.algorand.android.core.component.polling.accountdetail.data.model

import com.google.gson.annotations.SerializedName

internal data class AccountCacheRefreshResponse(
    @SerializedName("refresh") val shouldRefresh: Boolean?
)
