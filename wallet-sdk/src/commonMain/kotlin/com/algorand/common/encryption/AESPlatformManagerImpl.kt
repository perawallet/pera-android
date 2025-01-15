package com.algorand.common.encryption

internal expect class AESPlatformManagerImpl : AESPlatformManager {
    override fun encryptByteArray(data: ByteArray): ByteArray
    override fun decryptByteArray(encryptedData: ByteArray): ByteArray
    override fun encryptString(data: String): String
    override fun decryptString(encryptedData: String): String
}
