
package com.algorand.wallet.algosdk.transaction.sdk

interface SignHdKeyTransaction {
    fun signTransaction(
        transactionByteArray: ByteArray,
        seed: ByteArray,
        account: Int,
        change: Int,
        keyIndex: Int
    ): ByteArray?
}