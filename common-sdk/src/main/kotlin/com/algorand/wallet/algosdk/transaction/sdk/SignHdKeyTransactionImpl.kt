package com.algorand.wallet.algosdk.transaction.sdk

import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.crypto.Signature
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.util.Encoder
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import javax.inject.Inject

internal class SignHdKeyTransactionImpl @Inject constructor() : SignHdKeyTransaction {
    override fun signTransaction(
        transactionByteArray: ByteArray,
        seed: ByteArray,
        account: Int,
        change: Int,
        keyIndex: Int
    ): ByteArray? {
        return try {
            val tx = Encoder.decodeFromMsgPack(transactionByteArray, Transaction::class.java)

            val xHDWalletAPI = XHDWalletAPIAndroid(seed)
            val (accountIndex, changeIndex, keyIndex) = listOf(
                account.toUInt(),
                change.toUInt(),
                keyIndex.toUInt()
            )

            val pkAddress = Address(
                xHDWalletAPI.keyGen(
                    KeyContext.Address,
                    accountIndex,
                    changeIndex,
                    keyIndex
                )
            )

            val txSig = Signature(
                xHDWalletAPI.signAlgoTransaction(
                    KeyContext.Address, accountIndex, changeIndex, keyIndex, tx.bytesToSign()
                )
            )

            val stx = SignedTransaction(tx, txSig, tx.txID()).apply {
                if (tx.sender != pkAddress) authAddr(pkAddress)
            }

            return Encoder.encodeToMsgPack(stx)
        } catch (e: Exception) {
            null
        }
    }
}