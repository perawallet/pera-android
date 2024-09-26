package com.algorand.android.module.asb.backupprotocol.model

data class BackUpPayload(
    val nodeDeviceId: String,
    val accounts: List<BackUpAccount>,
    val mnemonics: List<String>
)
