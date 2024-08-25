package com.algorand.android.asb.component.restorebackup.validation

import com.algorand.android.asb.component.backupprotocol.model.BackupProtocolContent
import com.algorand.android.asb.component.restorebackup.domain.model.RestoreCipherTextResult

internal interface AsbBackUpProtocolContentValidator {
    fun validate(backUpProtocolContent: BackupProtocolContent?): RestoreCipherTextResult
}
