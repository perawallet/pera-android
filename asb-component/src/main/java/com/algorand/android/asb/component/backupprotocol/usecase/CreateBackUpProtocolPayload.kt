package com.algorand.android.asb.component.backupprotocol.usecase

import com.algorand.android.asb.component.backupprotocol.model.*
import com.algorand.android.asb.component.backupprotocol.model.BackUpProtocolPayload

internal interface CreateBackUpProtocolPayload {
    operator fun invoke(nodeDeviceId: String, accounts: List<BackUpAccount>): BackUpProtocolPayload
}
