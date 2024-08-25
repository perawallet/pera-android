package com.algorand.android.account.localaccount.data.mapper.model.ledgerble

import com.algorand.android.account.localaccount.data.database.model.LedgerBleEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
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
