package com.algorand.android.module.asb.restorebackup.validation

import com.algorand.android.module.asb.backupprotocol.model.BackupProtocolContent
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreCipherTextResult

internal interface AsbBackUpProtocolContentValidator {
    fun validate(backUpProtocolContent: BackupProtocolContent?): RestoreCipherTextResult
}
