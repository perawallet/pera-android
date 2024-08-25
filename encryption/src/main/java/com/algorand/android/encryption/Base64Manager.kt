package com.algorand.android.encryption

internal interface Base64Manager {

    fun encode(byteArray: ByteArray): String

    fun decode(value: String): ByteArray
}
