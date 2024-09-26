package com.algorand.android.module.algosdk

interface AlgoSdkEncryption {
    fun encryptContent(content: ByteArray, key: ByteArray): String?

    fun decryptContent(encryptedContent: String, key: ByteArray): String?
}
