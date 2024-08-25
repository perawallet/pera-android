package com.algorand.android.asb.component.restorebackup.validation

interface AsbFileContentValidator {
    fun isBackupFileTypeValid(fileType: String?): Boolean
}
