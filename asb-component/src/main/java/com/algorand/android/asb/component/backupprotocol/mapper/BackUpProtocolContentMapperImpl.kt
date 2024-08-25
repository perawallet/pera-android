package com.algorand.android.asb.component.backupprotocol.mapper

import com.algorand.android.asb.component.backupprotocol.BackUpConstants.BACKUP_PROTOCOL_SUITE
import com.algorand.android.asb.component.backupprotocol.BackUpConstants.BACKUP_PROTOCOL_VERSION
import com.algorand.android.asb.component.backupprotocol.model.BackupProtocolContent
import javax.inject.Inject

internal class BackUpProtocolContentMapperImpl @Inject constructor() : BackUpProtocolContentMapper {

    override fun invoke(cipherText: String): BackupProtocolContent {
        return BackupProtocolContent(
            version = BACKUP_PROTOCOL_VERSION,
            suite = BACKUP_PROTOCOL_SUITE,
            cipherText = cipherText
        )
    }
}
