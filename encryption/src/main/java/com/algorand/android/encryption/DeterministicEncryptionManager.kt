package com.algorand.android.encryption

import javax.inject.Inject

internal class DeterministicEncryptionManager @Inject constructor(
//    private val deterministicAead: DeterministicAead,
    private val base64Manager: Base64Manager
) : EncryptionManager {

    override fun encrypt(value: String): String {
        // TODO Enable encryption
//        val encryptedValue = deterministicAead.encryptDeterministically(value.toByteArray(Charsets.UTF_8), null)
//        return base64Manager.encode(encryptedValue)
        return value
    }

    override fun decrypt(value: String): String {
        // TODO Enable encryption
//        val decodedValue = base64Manager.decode(value)
//        val decryptedValue = deterministicAead.decryptDeterministically(decodedValue, null)
//        return String(decryptedValue, Charsets.UTF_8)
        return value
    }
}
