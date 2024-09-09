package com.algorand.android.account.localaccount.data.mapper.model.noauth

import com.algorand.android.account.localaccount.data.database.model.NoAuthEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class NoAuthMapperImpl(
    private val encryptionManager: EncryptionManager
) : NoAuthMapper {

    override fun invoke(entity: NoAuthEntity): LocalAccount.NoAuth {
        return LocalAccount.NoAuth(
            address = encryptionManager.decrypt(entity.encryptedAddress)
        )
    }
}
