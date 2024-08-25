package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.LedgerBleEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
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
