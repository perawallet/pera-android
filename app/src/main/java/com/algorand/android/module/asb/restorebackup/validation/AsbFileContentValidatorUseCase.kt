package com.algorand.android.module.asb.restorebackup.validation

import com.algorand.android.module.asb.utils.AsbBackUpConstants.IMPORT_BACKUP_FILE_MIME_TYPES
import javax.inject.Inject

internal class AsbFileContentValidatorUseCase @Inject constructor() : AsbFileContentValidator {

    override fun isBackupFileTypeValid(fileType: String?): Boolean {
        return IMPORT_BACKUP_FILE_MIME_TYPES.contains(fileType)
    }
}
