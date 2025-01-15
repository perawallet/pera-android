package com.algorand.common.encryption

internal actual class AESPlatformManagerImpl : AESPlatformManager {

    private val keyAlias = "MyAESKey"
    private val keySize = 256

    private fun generateKeyIfNeeded() {
//        val attributes = mapOf(
//            kSecAttrKeyType to kSecAttrKeyTypeAES,
//            kSecAttrKeySizeInBits to keySize,
//            kSecAttrIsPermanent to true,
//            kSecAttrApplicationTag to keyAlias
//        )
//        val status = SecKeyGenerateSymmetric(attributes, null)
//        if (status != errSecSuccess) {
//            throw IllegalStateException("Failed to generate key")
//        }
    }

    private fun getKey() {
//        val query = mapOf(
//            kSecClass to kSecClassKey,
//            kSecAttrApplicationTag to keyAlias,
//            kSecReturnData to true
//        )
//        memScoped {
//            val result = alloc<CFTypeRefVar>()
//            val status = SecItemCopyMatching(query, result.ptr)
//            if (status != errSecSuccess) {
//                throw IllegalStateException("Failed to retrieve key")
//            }
//            return result.value as NSData
//        }
    }

    actual override fun encryptByteArray(data: ByteArray): ByteArray {
        // TODO add in iOS portion later
        return data
//        generateKeyIfNeeded()
//        val key = getKey()
//        val iv = ByteArray(12) { it.toByte() } // Random IV
//        val encryptedData = ByteArray(data.size + 16) // Adjust for GCM overhead
//        data.usePinned { pinnedData ->
//            encryptedData.usePinned { pinnedEncryptedData ->
//                val status = CCCrypt(
//                    operation = kCCEncrypt,
//                    algorithm = kCCAlgorithmAES,
//                    options = kCCOptionPKCS7Padding,
//                    key = key.bytes,
//                    keyLength = key.length.toULong(),
//                    iv = iv,
//                    dataIn = pinnedData.addressOf(0),
//                    dataInLength = data.size.toULong(),
//                    dataOut = pinnedEncryptedData.addressOf(0),
//                    dataOutAvailable = encryptedData.size.toULong(),
//                    dataOutMoved = null
//                )
//                if (status != kCCSuccess) {
//                    throw IllegalStateException("Encryption failed")
//                }
//            }
//        }
//        return iv + encryptedData
    }

    actual override fun decryptByteArray(encryptedData: ByteArray): ByteArray {
        // TODO add in iOS portion later
        return encryptedData
//        val iv = encryptedData.copyOfRange(0, 12)
//        val cipherData = encryptedData.copyOfRange(12, encryptedData.size)
//        val key = getKey()
//        val decryptedData = ByteArray(cipherData.size)
//        cipherData.usePinned { pinnedCipherData ->
//            decryptedData.usePinned { pinnedDecryptedData ->
//                val status = CCCrypt(
//                    operation = kCCDecrypt,
//                    algorithm = kCCAlgorithmAES,
//                    options = kCCOptionPKCS7Padding,
//                    key = key.bytes,
//                    keyLength = key.length.toULong(),
//                    iv = iv,
//                    dataIn = pinnedCipherData.addressOf(0),
//                    dataInLength = cipherData.size.toULong(),
//                    dataOut = pinnedDecryptedData.addressOf(0),
//                    dataOutAvailable = decryptedData.size.toULong(),
//                    dataOutMoved = null
//                )
//                if (status != kCCSuccess) {
//                    throw IllegalStateException("Decryption failed")
//                }
//            }
//        }
//        return decryptedData
    }

    actual override fun encryptString(data: String): String {
        return data
    }

    actual override fun decryptString(encryptedData: String): String {
        return encryptedData
    }
}
