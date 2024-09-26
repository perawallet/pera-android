package com.algorand.android.module.asb.restorebackup.domain.usecase

import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult

interface RestoreBackUpPayload {
    operator fun invoke(keyInput: String, cipherText: String): RestoreBackUpPayloadResult
}
