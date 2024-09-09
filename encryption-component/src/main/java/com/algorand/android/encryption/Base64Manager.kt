package com.algorand.android.encryption

interface Base64Manager {

    fun encode(byteArray: ByteArray): String

    fun decode(value: String): ByteArray
}
