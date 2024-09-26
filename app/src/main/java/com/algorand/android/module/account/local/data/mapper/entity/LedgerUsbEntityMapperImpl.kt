package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class LedgerUsbEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : LedgerUsbEntityMapper {

    override fun invoke(localAccount: LocalAccount.LedgerUsb): LedgerUsbEntity {
        return LedgerUsbEntity(
            encryptedAddress = encryptionManager.encrypt(localAccount.address),
            ledgerUsbId = localAccount.deviceId,
            accountIndexInLedger = localAccount.indexInLedger
        )
    }
}
