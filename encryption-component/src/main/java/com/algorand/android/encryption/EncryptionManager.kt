package com.algorand.android.encryption

interface EncryptionManager {

    fun encrypt(value: String): String

    fun decrypt(value: String): String
}
