package com.algorand.android.asb.component.restorebackup.domain.model

import com.algorand.android.asb.component.backupprotocol.model.BackUpAccount

sealed interface RestoreBackUpPayloadResult {
    data object UnableToCreateCipherKey : RestoreBackUpPayloadResult
    data object UnableToRestoreAccounts : RestoreBackUpPayloadResult
    data class Success(val accounts: List<BackUpAccount>) : RestoreBackUpPayloadResult
}
