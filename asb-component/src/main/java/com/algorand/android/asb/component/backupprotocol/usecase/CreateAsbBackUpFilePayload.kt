package com.algorand.android.asb.component.backupprotocol.usecase

import com.algorand.android.asb.component.backupprotocol.model.BackUpPayload

interface CreateAsbBackUpFilePayload {
    operator fun invoke(payload: BackUpPayload): String?
}
