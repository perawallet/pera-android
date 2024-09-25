package com.algorand.android.module.deeplink.model

import com.google.gson.annotations.SerializedName

internal data class WebQrCode(
    @SerializedName("version") val version: String,
    @SerializedName("action") val action: String,
    @SerializedName("platform") val platform: String?,
    @SerializedName("backupId") val backupId: String,
    @SerializedName("modificationKey") val modificationKey: String?,
    @SerializedName("encryptionKey") val encryptionKey: String,
)
