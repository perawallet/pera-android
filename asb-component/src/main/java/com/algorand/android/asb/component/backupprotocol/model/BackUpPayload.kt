package com.algorand.android.asb.component.backupprotocol.model

data class BackUpPayload(
    val nodeDeviceId: String,
    val accounts: List<BackUpAccount>,
    val mnemonics: List<String>
)
