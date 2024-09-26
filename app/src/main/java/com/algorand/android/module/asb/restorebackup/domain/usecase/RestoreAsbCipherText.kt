package com.algorand.android.module.asb.restorebackup.domain.usecase

import com.algorand.android.module.asb.restorebackup.domain.model.RestoreCipherTextResult

interface RestoreAsbCipherText {
    operator fun invoke(encryptedPayload: String): RestoreCipherTextResult
}
