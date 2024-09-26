package com.algorand.android.module.asb.restorebackup.domain.model

import com.algorand.android.module.asb.backupprotocol.model.BackUpAccount

sealed interface RestoreBackUpPayloadResult {
    data object UnableToCreateCipherKey : RestoreBackUpPayloadResult
    data object UnableToRestoreAccounts : RestoreBackUpPayloadResult
    data class Success(val accounts: List<BackUpAccount>) : RestoreBackUpPayloadResult
}
