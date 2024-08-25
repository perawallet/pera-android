package com.algorand.android.asb.component.restorebackup.validation

import com.algorand.android.asb.component.utils.AsbBackUpConstants.IMPORT_BACKUP_FILE_MIME_TYPES
import javax.inject.Inject

internal class AsbFileContentValidatorUseCase @Inject constructor() : AsbFileContentValidator {

    override fun isBackupFileTypeValid(fileType: String?): Boolean {
        return IMPORT_BACKUP_FILE_MIME_TYPES.contains(fileType)
    }
}
