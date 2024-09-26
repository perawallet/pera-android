package com.algorand.android.module.encryption

interface EncryptionManager {

    fun encrypt(value: String): String

    fun decrypt(value: String): String
}
