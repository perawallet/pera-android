package com.algorand.android.module.asb.backupprotocol.mapper

import com.algorand.android.module.asb.backupprotocol.BackUpConstants.BACKUP_PROTOCOL_SUITE
import com.algorand.android.module.asb.backupprotocol.BackUpConstants.BACKUP_PROTOCOL_VERSION
import com.algorand.android.module.asb.backupprotocol.model.BackupProtocolContent
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
