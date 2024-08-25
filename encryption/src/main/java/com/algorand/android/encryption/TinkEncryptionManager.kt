package com.algorand.android.encryption

import com.google.crypto.tink.Aead
import javax.inject.Inject

internal class TinkEncryptionManager @Inject constructor(
    private val aead: Aead,
    private val base64Manager: Base64Manager
) : EncryptionManager {

    override fun encrypt(value: String): String {
        val encryptedValue = aead.encrypt(value.toByteArray(Charsets.UTF_8), null)
        return base64Manager.encode(encryptedValue)
    }

    override fun decrypt(value: String): String {
        val decodedValue = base64Manager.decode(value)
        val decryptedValue = aead.decrypt(decodedValue, null)
        return String(decryptedValue, Charsets.UTF_8)
    }
}
