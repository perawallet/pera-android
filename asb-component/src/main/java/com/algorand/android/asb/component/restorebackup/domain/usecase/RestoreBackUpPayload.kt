package com.algorand.android.asb.component.restorebackup.domain.usecase

import com.algorand.android.asb.component.restorebackup.domain.model.RestoreBackUpPayloadResult

interface RestoreBackUpPayload {
    operator fun invoke(keyInput: String, cipherText: String): RestoreBackUpPayloadResult
}
