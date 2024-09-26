package com.algorand.android.module.account.local.data.mapper.model.noauth

import com.algorand.android.module.account.local.data.database.model.NoAuthEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount
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
