@file:Suppress("MagicNumber")
package com.algorand.android.module.asb.backupprotocol.model

data class BackUpAccount(
    val address: String,
    val name: String,
    val secretKey: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BackUpAccount

        if (address != other.address) return false
        return secretKey.contentEquals(other.secretKey)
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + secretKey.contentHashCode()
        return result
    }
}
