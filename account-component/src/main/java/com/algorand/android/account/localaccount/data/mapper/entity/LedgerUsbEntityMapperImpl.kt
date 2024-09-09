package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.LedgerUsbEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
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
