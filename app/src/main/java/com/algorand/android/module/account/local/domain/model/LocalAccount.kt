@file:Suppress("MagicNumber")

package com.algorand.android.module.account.local.domain.model

sealed interface LocalAccount {

    val address: String

    data class Algo25(
        override val address: String,
        val secretKey: ByteArray
    ) : LocalAccount {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Algo25

            if (address != other.address) return false
            return secretKey.contentEquals(other.secretKey)
        }

        override fun hashCode(): Int {
            var result = address.hashCode()
            result = 31 * result + secretKey.contentHashCode()
            return result
        }
    }

    data class LedgerBle(
        override val address: String,
        val deviceMacAddress: String,
        val indexInLedger: Int
    ) : LocalAccount

    data class LedgerUsb(
        override val address: String,
        val deviceId: Int,
        val indexInLedger: Int
    ) : LocalAccount

    data class NoAuth(
        override val address: String
    ) : LocalAccount
}
