package com.algorand.android.module.asb.restorebackup.validation

interface AsbFileContentValidator {
    fun isBackupFileTypeValid(fileType: String?): Boolean
}
