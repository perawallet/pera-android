package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.Algo25Entity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.Base64Manager
import com.algorand.android.encryption.EncryptionManager

internal class Algo25EntityMapperImpl(
    private val deterministicEncryptionManager: EncryptionManager,
    private val nondeterministicEncryptionManager: EncryptionManager,
    private val base64Manager: Base64Manager
) : Algo25EntityMapper {

    override fun invoke(localAccount: LocalAccount.Algo25): Algo25Entity {
        val base64SecretKey = base64Manager.encode(localAccount.secretKey)
        return Algo25Entity(
            encryptedAddress = deterministicEncryptionManager.encrypt(localAccount.address),
            encryptedSecretKey = nondeterministicEncryptionManager.encrypt(base64SecretKey)
        )
    }
}
