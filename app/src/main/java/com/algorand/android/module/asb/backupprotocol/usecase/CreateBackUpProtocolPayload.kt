package com.algorand.android.module.asb.backupprotocol.usecase

import com.algorand.android.module.asb.backupprotocol.model.BackUpAccount
import com.algorand.android.module.asb.backupprotocol.model.BackUpProtocolPayload

internal interface CreateBackUpProtocolPayload {
    operator fun invoke(nodeDeviceId: String, accounts: List<BackUpAccount>): BackUpProtocolPayload
}
