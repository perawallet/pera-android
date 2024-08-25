package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.NoAuthEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class NoAuthEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : NoAuthEntityMapper {

    override fun invoke(localAccount: LocalAccount.NoAuth): NoAuthEntity {
        return NoAuthEntity(
            encryptedAddress = encryptionManager.encrypt(localAccount.address)
        )
    }
}
