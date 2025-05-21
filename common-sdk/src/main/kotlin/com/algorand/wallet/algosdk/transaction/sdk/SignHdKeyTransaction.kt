
package com.algorand.wallet.algosdk.transaction.sdk

interface SignHdKeyTransaction {
    fun signTransaction(
        transactionByteArray: ByteArray,
        seed: ByteArray,
        account: Int,
        change: Int,
        key: Int
    ): ByteArray?

    fun signLegacyArbitaryData(
        transactionByteArray: ByteArray,
        seed: ByteArray,
        account: Int,
        change: Int,
        key: Int
    ): ByteArray?
}
