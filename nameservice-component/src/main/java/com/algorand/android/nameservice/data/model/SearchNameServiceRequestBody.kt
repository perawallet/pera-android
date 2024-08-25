package com.algorand.android.nameservice.data.model

import com.google.gson.annotations.SerializedName

internal data class SearchNameServiceRequestBody(
    @SerializedName("account_addresses")
    val accountAddresses: List<String>
)
