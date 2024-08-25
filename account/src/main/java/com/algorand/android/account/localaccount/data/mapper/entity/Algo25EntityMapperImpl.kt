package com.algorand.android.account.localaccount.data.mapper.entity

import android.util.Base64
import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class Algo25EntityMapperImpl(
    private val deterministicEncryptionManager: EncryptionManager,
    private val nondeterministicEncryptionManager: EncryptionManager
) : Algo25EntityMapper {

    override fun invoke(localAccount: LocalAccount.Algo25): Algo25Entity {
        val base64SecretKey = Base64.encodeToString(localAccount.secretKey, Base64.NO_WRAP)
        return Algo25Entity(
            encryptedAddress = deterministicEncryptionManager.encrypt(localAccount.address),
            encryptedSecretKey = nondeterministicEncryptionManager.encrypt(base64SecretKey)
        )
    }
}
