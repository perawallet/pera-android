package com.algorand.android.module.asb.restorebackup.validation

import com.algorand.android.module.asb.backupprotocol.BackUpConstants.BACKUP_PROTOCOL_SUITE
import com.algorand.android.module.asb.backupprotocol.BackUpConstants.BACKUP_PROTOCOL_VERSION
import com.algorand.android.module.asb.backupprotocol.model.BackupProtocolContent
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreCipherTextResult
import javax.inject.Inject

internal class AsbBackUpProtocolContentValidatorUseCase @Inject constructor() : AsbBackUpProtocolContentValidator {

    override fun validate(backUpProtocolContent: BackupProtocolContent?): RestoreCipherTextResult {

        return when {
            backUpProtocolContent == null -> RestoreCipherTextResult.UnableToParseFile
            backUpProtocolContent.version == null -> RestoreCipherTextResult.MissingVersion
            backUpProtocolContent.suite == null -> RestoreCipherTextResult.MissingSuite
            backUpProtocolContent.cipherText == null -> RestoreCipherTextResult.MissingCipherText
            isBackupVersionValid(backUpProtocolContent.version).not() -> RestoreCipherTextResult.InvalidBackUpVersion
            isBackupSuiteValid(backUpProtocolContent.suite).not() -> RestoreCipherTextResult.InvalidSuite
            else -> RestoreCipherTextResult.Success(backUpProtocolContent.cipherText)
        }
    }

    private fun isBackupVersionValid(backupVersion: String?): Boolean {
        return backupVersion == BACKUP_PROTOCOL_VERSION
    }

    private fun isBackupSuiteValid(backupSuite: String?): Boolean {
        return backupSuite == BACKUP_PROTOCOL_SUITE
    }
}
