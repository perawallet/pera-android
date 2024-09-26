package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.LedgerBleEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class LedgerBleEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : LedgerBleEntityMapper {

    override fun invoke(localAccount: LocalAccount.LedgerBle): LedgerBleEntity {
        return LedgerBleEntity(
            encryptedAddress = encryptionManager.encrypt(localAccount.address),
            deviceMacAddress = localAccount.deviceMacAddress,
            accountIndexInLedger = localAccount.indexInLedger
        )
    }
}
