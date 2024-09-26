package com.algorand.android.module.account.local.data.mapper.model.ledgerusb

import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.encryption.EncryptionManager

internal class LedgerUsbMapperImpl(
    private val encryptionManager: EncryptionManager
) : LedgerUsbMapper {

    override fun invoke(entity: LedgerUsbEntity): LocalAccount.LedgerUsb {
        return LocalAccount.LedgerUsb(
            address = encryptionManager.decrypt(entity.encryptedAddress),
            deviceId = entity.ledgerUsbId,
            indexInLedger = entity.accountIndexInLedger
        )
    }
}
