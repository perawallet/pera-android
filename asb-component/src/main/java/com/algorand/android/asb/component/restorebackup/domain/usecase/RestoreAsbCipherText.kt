package com.algorand.android.asb.component.restorebackup.domain.usecase

import com.algorand.android.asb.component.restorebackup.domain.model.RestoreCipherTextResult

interface RestoreAsbCipherText {
    operator fun invoke(encryptedPayload: String): RestoreCipherTextResult
}
