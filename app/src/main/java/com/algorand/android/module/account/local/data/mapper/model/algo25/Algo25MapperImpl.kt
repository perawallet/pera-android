package com.algorand.android.module.account.local.data.mapper.model.algo25

import com.algorand.android.module.account.local.data.database.model.Algo25Entity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.Base64Manager
import com.algorand.android.encryption.EncryptionManager

internal class Algo25MapperImpl(
    private val deterministicEncryptionManager: EncryptionManager,
    private val nondeterministicEncryptionManager: EncryptionManager,
    private val base64Manager: Base64Manager
) : Algo25Mapper {

    override fun invoke(entity: Algo25Entity): LocalAccount.Algo25 {
        val base64SecretKey = nondeterministicEncryptionManager.decrypt(entity.encryptedSecretKey)
        val decodedSecretKey = base64Manager.decode(base64SecretKey)
        return LocalAccount.Algo25(
            address = deterministicEncryptionManager.decrypt(entity.encryptedAddress),
            secretKey = decodedSecretKey
        )
    }
}
