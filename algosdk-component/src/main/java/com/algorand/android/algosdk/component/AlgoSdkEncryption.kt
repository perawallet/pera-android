package com.algorand.android.algosdk.component

interface AlgoSdkEncryption {
    fun encryptContent(content: ByteArray, key: ByteArray): String?

    fun decryptContent(encryptedContent: String, key: ByteArray): String?
}
