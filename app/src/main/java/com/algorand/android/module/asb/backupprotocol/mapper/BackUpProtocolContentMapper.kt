package com.algorand.android.module.asb.backupprotocol.mapper

import com.algorand.android.module.asb.backupprotocol.model.BackupProtocolContent

internal interface BackUpProtocolContentMapper {
    operator fun invoke(cipherText: String): BackupProtocolContent
}
