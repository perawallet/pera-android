package com.algorand.android.asb.component.backupprotocol.model

import com.google.gson.annotations.SerializedName

internal data class BackUpProtocolElement(
    @SerializedName("address") val address: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("account_type") val accountType: String?,
    @SerializedName("private_key") val privateKey: String?,
    @SerializedName("metadata") val metadata: String?
)
