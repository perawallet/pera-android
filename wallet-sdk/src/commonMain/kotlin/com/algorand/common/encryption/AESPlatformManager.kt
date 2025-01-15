package com.algorand.common.encryption

interface AESPlatformManager {
    fun encryptByteArray(data: ByteArray): ByteArray
    fun decryptByteArray(encryptedData: ByteArray): ByteArray
    fun encryptString(data: String): String
    fun decryptString(encryptedData: String): String
}