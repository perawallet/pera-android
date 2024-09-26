package com.algorand.android.module.asb.backupprotocol.usecase

import com.algorand.android.module.asb.backupprotocol.model.BackUpPayload

interface CreateAsbBackUpFilePayload {
    operator fun invoke(payload: BackUpPayload): String?
}
