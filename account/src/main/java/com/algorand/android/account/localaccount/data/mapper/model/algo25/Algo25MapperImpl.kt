package com.algorand.android.account.localaccount.data.mapper.model.algo25

import android.util.Base64
import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager

internal class Algo25MapperImpl(
    private val deterministicEncryptionManager: EncryptionManager,
    private val nondeterministicEncryptionManager: EncryptionManager
) : Algo25Mapper {

    override fun invoke(entity: Algo25Entity): LocalAccount.Algo25 {
        val base64SecretKey = nondeterministicEncryptionManager.decrypt(entity.encryptedSecretKey)
        val decodedSecretKey = Base64.decode(base64SecretKey, Base64.NO_WRAP)
        return LocalAccount.Algo25(
            address = deterministicEncryptionManager.decrypt(entity.encryptedAddress),
            secretKey = decodedSecretKey
        )
    }
}
