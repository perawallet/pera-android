package com.algorand.android.module.account.local.data.mapper.model.ledgerble

import com.algorand.android.module.account.local.data.database.model.LedgerBleEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class LedgerBleMapperImpl(
    private val encryptionManager: EncryptionManager
) : LedgerBleMapper {

    override fun invoke(entity: LedgerBleEntity): LocalAccount.LedgerBle {
        return LocalAccount.LedgerBle(
            address = encryptionManager.decrypt(entity.encryptedAddress),
            deviceMacAddress = entity.deviceMacAddress,
            indexInLedger = entity.accountIndexInLedger
        )
    }
}
