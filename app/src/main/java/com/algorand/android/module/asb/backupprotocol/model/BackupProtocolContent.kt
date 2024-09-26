package com.algorand.android.module.asb.backupprotocol.model

import com.google.gson.annotations.SerializedName

internal data class BackupProtocolContent(
    @SerializedName("version") val version: String?,
    @SerializedName("suite") val suite: String?,
    @SerializedName("ciphertext") val cipherText: String?
)
