package com.algorand.android.asb.component.backupprotocol.mapper

import com.algorand.android.asb.component.backupprotocol.model.BackupProtocolContent

internal interface BackUpProtocolContentMapper {
    operator fun invoke(cipherText: String): BackupProtocolContent
}
